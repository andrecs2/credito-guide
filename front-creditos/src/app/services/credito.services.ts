import { Injectable , inject} from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ConfigService } from '../services/config'; 

export interface Credito {
  numeroCredito: string;
  numeroNfse: string;
  dataConstituicao: string;
  valorIssqn: number;
  tipoCredito?: string;
  simplesNacional: string;
  aliquota: number;
  valorFaturado: number;
  valorDeducao: number;
  baseCalculo: number;
}

export interface ApiError {
  message: string;
  status: number;
  statusText: string;
  url?: string;
  timestamp: Date;
}

@Injectable({
  providedIn: 'root'
})
export class CreditoService {
  private config = inject(ConfigService);
  
  private http = inject(HttpClient);


  getCreditosPorNfse(numeroNfse: string): Observable<Credito[]> {
    return this.http.get<Credito[]>(`/api/creditos/${numeroNfse}`)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          const apiError = this.createApiError(error, `Erro ao buscar créditos para NFS-e: ${numeroNfse}`);
          return throwError(() => apiError);
        })
      );
  }

  getCreditoPorNumero(numeroCredito: string): Observable<Credito> {
    return this.http.get<Credito>(`/api/creditos/credito/${numeroCredito}`)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          const apiError = this.createApiError(error, `Erro ao buscar crédito: ${numeroCredito}`);
          return throwError(() => apiError);
        })
      );
  }

  private createApiError(error: HttpErrorResponse, context: string): ApiError {
    let message = 'Erro desconhecido';
    
    console.group(`❌ ERRO NA REQUISIÇÃO - ${context}`);
    console.log('Status:', error.status);
    console.log('Status Text:', error.statusText);
    console.log('URL:', error.url);
    console.log('Headers:', error.headers);
    console.log('Error Object:', error.error);
    console.log('Full Error:', error);
    console.groupEnd();
    
    if (error.error instanceof ErrorEvent) {
      message = `Erro de conexão: ${error.error.message}`;
    } else {
      switch (error.status) {
        case 0:
          message = 'Servidor não está respondendo. Verifique se o servidor está online.';
          break;
        case 400:
          message = 'Requisição inválida. Verifique os dados enviados.';
          break;
        case 401:
          message = 'Acesso não autorizado. Faça login novamente.';
          break;
        case 403:
          message = 'Acesso proibido. Você não tem permissão para acessar este recurso.';
          break;
        case 404:
          message = `Registro não encontrado. ${context.split(':')[1]?.trim()}`;
          break;
        case 408:
          message = 'Tempo de requisição esgotado. Tente novamente.';
          break;
        case 500:
          message = 'Erro interno do servidor. Entre em contato com o suporte.';
          break;
        case 502:
          message = 'Bad Gateway. Problema no servidor intermediário.';
          break;
        case 503:
          message = 'Serviço indisponível. Tente novamente mais tarde.';
          break;
        case 504:
          message = 'Gateway Timeout. O servidor está demorando para responder.';
          break;
        default:
          message = `Erro ${error.status}: ${error.message || 'Erro desconhecido'}`;
      }
      
      if (error.error && typeof error.error === 'object') {
        if (error.error.message) {
          message = error.error.message;
        } else if (error.error.error) {
          message = error.error.error;
        }
      }
    }
    
    return {
      message,
      status: error.status,
      statusText: error.statusText,
      url: error.url || undefined,
      timestamp: new Date()
    };
  }


}