import { Routes } from '@angular/router';
import { ConsultaCreditosComponent } from './components/consulta-creditos/consulta-creditos';

export const routes: Routes = [
  { path: '', component: ConsultaCreditosComponent, title: 'Consulta de Créditos' },
  { path: 'consulta', component: ConsultaCreditosComponent, title: 'Consulta de Créditos' },
  { path: '**', redirectTo: '', pathMatch: 'full' }
];