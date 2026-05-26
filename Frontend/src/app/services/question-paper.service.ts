import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { QuestionPaper } from '../model/question-paper.model';
import { Observable } from 'rxjs';
import baseUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class QuestionPaperService {

  private apiUrl = `${baseUrl}/questionPaper`; 

  constructor(private http: HttpClient) { }

  addQuestionPaper(questionPaper: any): Observable<QuestionPaper> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.post<QuestionPaper>(`${this.apiUrl}/add`, questionPaper,{headers});
  }

  updateQuestionPaper(id: number, questionPaper: any): Observable<QuestionPaper> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.put<QuestionPaper>(`${this.apiUrl}/update/${id}`, questionPaper,{headers});
  }

  getAllQuestionPapers(): Observable<QuestionPaper[]> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<QuestionPaper[]>(`${this.apiUrl}/getAll`,{headers});
  }

  deleteQuestionPaper(qid: number): Observable<void> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.delete<void>(`${this.apiUrl}/delete/${qid}`,{headers});
  }

  getQuestionPaperById(quizId: number): Observable<QuestionPaper> {
    const token = localStorage.getItem('token');
    const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
    return this.http.get<QuestionPaper>(`${this.apiUrl}/${quizId}`,{headers});
  }
}
