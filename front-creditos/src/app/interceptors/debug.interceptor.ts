import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent, HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

@Injectable()
export class DebugInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const startTime = Date.now();
    
    console.group(`🌐 REQUEST: ${req.method} ${req.url}`);
    console.log('Headers:', req.headers);
    console.log('Body:', req.body);
    
    return next.handle(req).pipe(
      tap({
        next: (event) => {
          if (event instanceof HttpResponse) {
            const duration = Date.now() - startTime;
            console.groupCollapsed(`✅ RESPONSE: ${req.method} ${req.url} (${duration}ms)`);
            console.log('Status:', event.status);
            console.log('Status Text:', event.statusText);
            console.log('Headers:', event.headers);
            console.log('Body:', event.body);
            console.groupEnd();
          }
        },
        error: (error: HttpErrorResponse) => {
          const duration = Date.now() - startTime;
          console.groupCollapsed(`❌ ERROR: ${req.method} ${req.url} (${duration}ms)`);
          console.log('Status:', error.status);
          console.log('Status Text:', error.statusText);
          console.log('Error:', error.error);
          console.log('Full Error:', error);
          console.groupEnd();
        },
        complete: () => {
          console.groupEnd();
        }
      })
    );
  }
}