import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private config: any = {};

  constructor() { }

  setConfig(data: any) {
    this.config = data;
  }

  get apiUrl(): string {
    return this.config?.API_URL || '';
  }

  get(key: string): any {
    return this.config[key];
  }
}