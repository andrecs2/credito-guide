import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import { CreditoService, Credito, ApiError } from '../../services/credito.services';

@Component({
  selector: 'app-consulta-creditos',
  templateUrl: './consulta-creditos.html',
  styleUrls: ['./consulta-creditos.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule]
})
export class ConsultaCreditosComponent implements OnInit {
  consultaForm!: FormGroup;
  creditos: Credito[] = [];
  creditoDetalhado: Credito | null = null;
  isLoading = false;
  errorMessage = '';
  errorDetails: ApiError | null = null;
  modoConsulta: 'nfse' | 'credito' = 'nfse';
  connectionStatus: { connected: boolean; message: string } | null = null;

  get numeroNfseControl(): AbstractControl | null {
    return this.consultaForm?.get('numeroNfse');
  }

  get numeroCreditoControl(): AbstractControl | null {
    return this.consultaForm?.get('numeroCredito');
  }

  constructor(
    private creditoService: CreditoService,
    private fb: FormBuilder,
      private cdr: ChangeDetectorRef 

  ) {}

  ngOnInit(): void {
    this.initForm();
  }

  initForm(): void {
    this.consultaForm = this.fb.group({
      numeroNfse: ['', [Validators.required, Validators.minLength(1)]],
      numeroCredito: ['', [Validators.required, Validators.minLength(1)]]
    });
      this.isLoading = false;
                    this.updateUI();

      this.cdr.detectChanges();

  }


  setModoConsulta(modo: 'nfse' | 'credito'): void {
    this.modoConsulta = modo;
    this.creditoDetalhado = null;
    this.creditos = [];
    this.errorMessage = '';
    this.errorDetails = null;
    this.isLoading =false;
    if (modo === 'nfse') {
      this.numeroCreditoControl?.setValue('');

      this.numeroCreditoControl?.clearValidators();
      this.numeroCreditoControl?.updateValueAndValidity();
      
      
      this.numeroNfseControl?.setValidators([Validators.required, Validators.minLength(1)]);
      this.numeroNfseControl?.updateValueAndValidity();

    } else {
      this.numeroNfseControl?.setValue('');
      
      this.numeroNfseControl?.clearValidators();
      this.numeroNfseControl?.updateValueAndValidity();
      
      this.numeroCreditoControl?.setValidators([Validators.required, Validators.minLength(1)]);
      this.numeroCreditoControl?.updateValueAndValidity();
    }
  }

  consultar(): void {
    if (this.consultaForm.invalid) {
      this.consultaForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.cdr.detectChanges();
    this.errorMessage = '';
    this.errorDetails = null;
    this.creditoDetalhado = null;
    this.creditos = [];


    if (this.modoConsulta === 'nfse') {
      const numeroNfse = this.numeroNfseControl?.value;
      console.log(`📋 Consultando NFS-e: ${numeroNfse}`);
      this.consultarPorNfse(numeroNfse);
    } else {
      const numeroCredito = this.numeroCreditoControl?.value;
      console.log(`💰 Consultando Crédito: ${numeroCredito}`);
      this.consultarPorCredito(numeroCredito);
    }
  }

  consultarPorNfse(numeroNfse: string): void {
    this.creditoService.getCreditosPorNfse(numeroNfse).subscribe({
      next: (data) => {
        console.log(`✅ Dados recebidos (${data.length} registros):`, data);
        this.creditos = data;
        this.isLoading = false;
        this.cdr.detectChanges();
        
        if (data.length === 0) {
          this.errorMessage = `Nenhum crédito encontrado para a NFS-e: ${numeroNfse}`;
          this.cdr.detectChanges(); 
          
        }
      },
      error: (error: ApiError) => {
        console.error('❌ Erro na consulta por NFS-e:', error);
        this.errorMessage = error.message;
        this.errorDetails = error;
        this.isLoading = false;
        this.cdr.detectChanges();
        
      }
    });
  }

  consultarPorCredito(numeroCredito: string): void {
    this.creditoService.getCreditoPorNumero(numeroCredito).subscribe({
      next: (data) => {
        console.log('✅ Dados do crédito recebidos:', data);
        this.creditoDetalhado = data;
        this.isLoading = false;
              this.cdr.detectChanges();

      },
      error: (error: ApiError) => {
        console.error('❌ Erro na consulta por crédito:', error);
        this.errorMessage = error.message;
        this.errorDetails = error;
        this.isLoading = false;
              this.cdr.detectChanges();
              this.updateUI();

        
      }
    });
  }

  limpar(): void {
    this.consultaForm.reset();
    this.creditos = [];
    this.creditoDetalhado = null;
    this.errorMessage = '';
    this.errorDetails = null;
    console.log('🧹 Formulário limpo');
  }


  getStatusClass(status: string): string {
    if (!status) return 'bg-secondary';
    
    switch (status.toUpperCase()) {
      case 'SIM':
        return 'bg-success';
      case 'NAO':
      default:
        return 'bg-secondary';
    }
  }

  formatarData(data: string): string {
    if (!data) return '-';
    try {
      const date = new Date(data);
      return date.toLocaleDateString('pt-BR');
    } catch {
      return data;
    }
  }

  formatarMoeda(valor: number): string {
    if (!valor && valor !== 0) return 'R$ 0,00';
    return valor.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  recarregarPagina(): void {
    window.location.reload();
  }
private updateUI() {
  this.cdr.detectChanges(); 
  setTimeout(() => {
    this.cdr.markForCheck(); 
  }, 0);
}
}