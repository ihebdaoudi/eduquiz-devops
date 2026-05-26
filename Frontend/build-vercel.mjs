import { execSync } from 'node:child_process';
import { cpSync, mkdirSync, rmSync } from 'node:fs';
import { join } from 'node:path';

console.log('Running Angular production build...');
execSync('npx ng build', { stdio: 'inherit' });

const source = join('dist', 'frontend', 'browser');
const target = 'browser';

rmSync(target, { recursive: true, force: true });
mkdirSync(target, { recursive: true });
cpSync(source, target, { recursive: true });

console.log(`Created Vercel output directory: ${target}/`);
