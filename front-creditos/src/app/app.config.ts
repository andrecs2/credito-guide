
import { ApplicationConfig, APP_INITIALIZER } from '@angular/core'; 
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { DebugInterceptor } from './interceptors/debug.interceptor';
import { provideHttpClient, withInterceptorsFromDi, HTTP_INTERCEPTORS, HttpClient } from '@angular/common/http';
import { ConfigService } from './services/config';
import { firstValueFrom } from 'rxjs'; 

export function configLoaderFactory(http: HttpClient, configService: ConfigService) {
  return () => firstValueFrom(
    http.get('/config.json')
  ).then(config => {
    configService.setConfig(config);
  });
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    {
      provide: HTTP_INTERCEPTORS,
      useClass: DebugInterceptor,
      multi: true
    },
    {
      provide: APP_INITIALIZER,
      useFactory: configLoaderFactory,
      multi: true,
      deps: [HttpClient, ConfigService]
    }
  ]
};