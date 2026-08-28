import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../core/auth.service';

@Component({
  selector: 'app-cadastro',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CadastroComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  protected readonly tipo = signal<'pf' | 'pj'>(inject(ActivatedRoute).snapshot.queryParamMap.get('tipo') === 'pj' ? 'pj' : 'pf');
  protected readonly loading = signal(false);
  protected readonly success = signal('');
  protected readonly error = signal('');
  protected readonly form = new FormGroup({
    nome: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    dataNascimento: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    cpf: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    nomeRazaoSocial: new FormControl('', { nonNullable: true }),
    nomeFantasia: new FormControl('', { nonNullable: true }),
    dataFundacao: new FormControl('', { nonNullable: true }),
    faturamentoAnual: new FormControl<number | null>(null),
    documento: new FormControl('', { nonNullable: true }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    telefone: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    endereco: new FormGroup({
      endereco: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
      numero: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
      cep: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
      bairro: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
      cidade: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
      uf: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(2), Validators.maxLength(2)] }),
    }),
    login: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    senha: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(6)] }),
  });

  protected escolher(tipo: 'pf' | 'pj'): void { this.tipo.set(tipo); this.form.reset(); this.success.set(''); this.error.set(''); }

  protected cadastrar(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    const value = this.form.getRawValue();
    this.loading.set(true); this.error.set(''); this.success.set('');
    const request = this.tipo() === 'pf'
      ? this.auth.cadastrarPF({ nome: value.nome, dataNascimento: value.dataNascimento, cpf: value.cpf, email: value.email, telefone: value.telefone, endereco: value.endereco, usuario: { login: value.login, senha: value.senha } })
      : this.auth.cadastrarPJ({ nomeRazaoSocial: value.nomeRazaoSocial, nomeFantasia: value.nomeFantasia, dataFundacao: value.dataFundacao || null, faturamentoAnual: value.faturamentoAnual ?? 0, documento: value.documento, email: value.email, telefone: value.telefone, endereco: value.endereco, usuario: { login: value.login, senha: value.senha } });
    request.subscribe({
      next: () => { this.loading.set(false); this.success.set('Cadastro realizado com sucesso. Você já pode entrar.'); setTimeout(() => void this.router.navigate(['/login']), 1200); },
      error: (error) => { this.loading.set(false); this.error.set(error?.error?.detail ?? 'Não foi possível concluir o cadastro. Confira os dados.'); },
    });
  }
}
