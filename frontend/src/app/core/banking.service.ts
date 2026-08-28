import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { take, tap } from 'rxjs';
import { Conta, Transacao, TransferenciaRequest } from './models';

@Injectable({ providedIn: 'root' })
export class BankingService {
  private readonly apiUrl = '/api';
  readonly conta = signal<Conta | null>(null);
  readonly historico = signal<Transacao[]>(this.readHistory());
  readonly mostrarValores = signal(sessionStorage.getItem('fourbank_show_values') !== 'false');

  constructor(private readonly http: HttpClient) {}

  carregarConta() {
    return this.http.get<Conta>(`${this.apiUrl}/contas`).pipe(tap((conta) => this.conta.set(conta)));
  }

  transferir(payload: TransferenciaRequest) {
    return this.http.post<Transacao>(`${this.apiUrl}/transacoes/transferencias`, payload).pipe(
      tap((transacao) => this.registrar(transacao)),
    );
  }

  pix(chavePix: string, valor: number) {
    return this.http.post<Transacao>(`${this.apiUrl}/transacoes/pix/${encodeURIComponent(chavePix)}/${valor}`, null).pipe(
      tap((transacao) => this.registrar(transacao)),
    );
  }

  depositar(valor: number) {
    return this.http.post<Transacao>(`${this.apiUrl}/transacoes/deposito/${valor}`, null).pipe(
      tap((transacao) => this.registrar(transacao)),
    );
  }

  sacar(valor: number) {
    return this.http.post<Transacao>(`${this.apiUrl}/transacoes/saque/${valor}`, null).pipe(
      tap((transacao) => this.registrar(transacao)),
    );
  }

  alternarVisibilidadeValores(): void {
    const mostrar = !this.mostrarValores();
    this.mostrarValores.set(mostrar);
    sessionStorage.setItem('fourbank_show_values', String(mostrar));
  }

  private registrar(transacao: Transacao): void {
    const atual = [transacao, ...this.historico()].slice(0, 50);
    this.historico.set(atual);
    sessionStorage.setItem('fourbank_history', JSON.stringify(atual));
    this.carregarConta().pipe(take(1)).subscribe();
  }

  private readHistory(): Transacao[] {
    try {
      return JSON.parse(sessionStorage.getItem('fourbank_history') ?? '[]') as Transacao[];
    } catch {
      return [];
    }
  }
}
