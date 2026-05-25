# Script de deploiement Kubernetes pour EduQuizPortal (Kind)
# Usage : .\deploy-k8s.ps1
# NOTE : Si tu as deja un cluster Kind sans les ports mappes, supprime-le d'abord :
#        kind delete cluster --name eduquiz-cluster

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$clusterName = "eduquiz-cluster"

Write-Host "=== Deploiement EduQuiz sur Kubernetes (Kind) ===" -ForegroundColor Cyan

# --- Verification des outils ---
if (-not (Get-Command kind -ErrorAction SilentlyContinue)) {
    Write-Error "Kind n'est pas installe. Installe-le : https://kind.sigs.k8s.io/docs/user/quick-start/#installation"
    exit 1
}
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "Docker n'est pas installe ou n'est pas dans le PATH."
    exit 1
}
if (-not (Get-Command kubectl -ErrorAction SilentlyContinue)) {
    Write-Error "kubectl n'est pas installe."
    exit 1
}

# --- Cluster Kind ---
$existingClusters = @()
try {
    $rawOutput = kind get clusters 2>&1
    $existingClusters = $rawOutput | Where-Object { $_ -is [string] }
} catch {
    $existingClusters = @()
}
if ($existingClusters -contains $clusterName) {
    Write-Host "[1/6] Cluster Kind '$clusterName' deja existant." -ForegroundColor Green
} else {
    Write-Host "[1/6] Creation du cluster Kind '$clusterName' avec mapping de ports..." -ForegroundColor Yellow
    kind create cluster --name $clusterName --config "$projectRoot\kind-config.yaml"
}

# --- Build Backend ---
Write-Host "[2/6] Compilation du backend (Maven)..." -ForegroundColor Yellow
Set-Location "$projectRoot\Backend"
if (Test-Path ".\mvnw.cmd") {
    .\mvnw.cmd clean package -DskipTests -q
} else {
    mvn clean package -DskipTests -q
}

Write-Host "[3/6] Build de l'image Docker backend..." -ForegroundColor Yellow
Set-Location $projectRoot
docker build -t backend-app:latest "$projectRoot\Backend"

# --- Build Frontend ---
Write-Host "[4/6] Build de l'image Docker frontend..." -ForegroundColor Yellow
docker build -t frontend-app:latest "$projectRoot\Frontend"

# --- Load images into Kind ---
Write-Host "[5/6] Chargement des images dans Kind..." -ForegroundColor Yellow
kind load docker-image backend-app:latest --name $clusterName
kind load docker-image frontend-app:latest --name $clusterName

# --- Deploy K8s manifests ---
Write-Host "[6/6] Application des ressources Kubernetes..." -ForegroundColor Yellow
kubectl apply -f "$projectRoot\k8s\namespace.yaml"
kubectl apply -R -f "$projectRoot\k8s\"

Start-Sleep -Seconds 5

Write-Host ""
Write-Host "=== Statut des pods ===" -ForegroundColor Cyan
kubectl get pods -n eduquiz

Write-Host ""
Write-Host "=== Statut des services ===" -ForegroundColor Cyan
kubectl get svc -n eduquiz

Write-Host ""
Write-Host "=== Deploiement termine ===" -ForegroundColor Green
Write-Host "Acces direct depuis ton navigateur (Windows) :" -ForegroundColor Yellow
Write-Host "  - Frontend    : http://localhost:30000"
Write-Host "  - Backend API : http://localhost:30080"
Write-Host "  - Grafana     : http://localhost:30300"
Write-Host "  - Prometheus  : http://localhost:30090"
Write-Host ""
Write-Host "Le frontend appelle le backend en interne via le proxy Nginx (/api)." -ForegroundColor DarkGray
