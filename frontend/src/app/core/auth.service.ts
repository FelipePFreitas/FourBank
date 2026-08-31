import {inject, Injectable, signal} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import { AuthTokenResponse, CadastroPFRequest, CadastroPJRequest } from './models';
import {Router} from '@angular/router';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = '/api';
  private readonly tokenKey = 'fourbank_access_token';
  private readonly expirationKey = 'fourbank_token_expires_at';
  private router = inject(Router);
  readonly token = signal<string | null>(this.tokenFromStorage());

  constructor(private readonly http: HttpClient) {}

  login(login: string, senha: string) {
    return this.http.post<AuthTokenResponse>(`${this.apiUrl}/auth/login`, { login, senha }).pipe(
      tap((response) => {
        sessionStorage.setItem(this.tokenKey, response.accessToken);
        sessionStorage.setItem(this.expirationKey, String(Date.now() + response.expiresInMillis));
        this.token.set(response.accessToken);
      }),
    );
  }

  cadastrarPF(payload: CadastroPFRequest) {
    return this.http.post(`${this.apiUrl}/clientes/pf`, payload);
  }

  cadastrarPJ(payload: CadastroPJRequest) {
    return this.http.post(`${this.apiUrl}/clientes/pj`, payload);
  }

  logout(): void {
    sessionStorage.removeItem(this.tokenKey);
    sessionStorage.removeItem(this.expirationKey);
    this.token.set(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    const token = this.token();
    const expiration = Number(sessionStorage.getItem(this.expirationKey));
    return Boolean(token && Number.isFinite(expiration) && expiration > Date.now());
  }

  private tokenFromStorage(): string | null {
    const token = sessionStorage.getItem(this.tokenKey);
    const expiration = Number(sessionStorage.getItem(this.expirationKey));
    if (!token || !Number.isFinite(expiration) || expiration <= Date.now()) {
      sessionStorage.removeItem(this.tokenKey);
      sessionStorage.removeItem(this.expirationKey);
      return null;
    }
    return token;
  }
}
