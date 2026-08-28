import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { BankingService } from '../../core/banking.service';

@Component({
  selector: 'app-operacoes',
  imports: [ReactiveFormsModule],
  templateUrl: './operacoes.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperacoesComponent {
  private readonly route = inject(ActivatedRoute);
  protected readonly banking = inject(BankingService);
  protected readonly loading = signal(false);
  protected readonly message = signal('');
  protected readonly error = signal('');
  protected readonly tipo = signal<'deposito' | 'saque' | 'pix' | 'transferencia'>('deposito');
  protected readonly valorForm = new FormGroup({ valor: new FormControl<number | null>(null, [Validators.required, Validators.min(0.01)]) });
  protected readonly pixForm = new FormGroup({
    chavePix: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    valor: new FormControl<number | null>(null, [Validators.required, Validators.min(0.01)]),
  });
  protected readonly transferenciaForm = new FormGroup({
    nome: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    documento: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    banco: new FormControl('FOURBANK', { nonNullable: true, validators: [Validators.required] }),
    agencia: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    conta: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    tipoConta: new FormControl<'CC' | 'CP'>('CC', { nonNullable: true }),
    valor: new FormControl<number | null>(null, [Validators.required, Validators.min(0.01)]),
    agendadaPara: new FormControl('', { nonNullable: true }),
  });

  constructor() {
    this.route.queryParamMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      const tipo = params.get('tipo');
      if (tipo === 'deposito' || tipo === 'saque' || tipo === 'pix' || tipo === 'transferencia') {
        this.tipo.set(tipo);
      }
    });
  }

  protected selecionar(tipo: 'deposito' | 'saque' | 'pix' | 'transferencia'): void { this.tipo.set(tipo); this.message.set(''); this.error.set(''); }
  protected executarValor(): void {
    if (this.valorForm.invalid) { this.valorForm.markAllAsTouched(); return; }
    const valor = this.valorForm.controls.valor.value!;
    this.confirmar(`Confirma ${this.tipo() === 'deposito' ? 'o depósito' : 'o saque'} de R$ ${valor.toFixed(2)}?`, () => {
      const request = this.tipo() === 'deposito' ? this.banking.depositar(valor) : this.banking.sacar(valor);
      this.enviar(request);
    });
  }
  protected executarPix(): void {
    if (this.pixForm.invalid) { this.pixForm.markAllAsTouched(); return; }
    const { chavePix, valor } = this.pixForm.getRawValue();
    this.confirmar(`Confirma o Pix de R$ ${valor!.toFixed(2)}?`, () => this.enviar(this.banking.pix(chavePix, valor!)));
  }
  protected executarTransferencia(): void {
    if (this.transferenciaForm.invalid) { this.transferenciaForm.markAllAsTouched(); return; }
    const raw = this.transferenciaForm.getRawValue();
    this.confirmar(`Confirma a transferência de R$ ${raw.valor!.toFixed(2)}?`, () => this.enviar(this.banking.transferir({ ...raw, valor: raw.valor!, agendadaPara: raw.agendadaPara || null })));
  }
  private confirmar(texto: string, action: () => void): void { if (window.confirm(texto)) { this.loading.set(true); this.message.set(''); this.error.set(''); action(); } }
  private enviar(request: ReturnType<BankingService['depositar']>): void {
    request.subscribe({
      next: (transacao) => { this.loading.set(false); this.message.set(transacao.descricao ?? 'Operação realizada com sucesso.'); },
      error: (error) => { this.loading.set(false); this.error.set(error?.error?.detail ?? 'A operação não pôde ser concluída.'); },
    });
  }
}
