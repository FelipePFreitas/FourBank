import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { BankingService } from '../../core/banking.service';

@Component({
  selector: 'app-dashboard',
  imports: [CurrencyPipe, RouterLink],
  templateUrl: './dashboard.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {
  protected readonly banking = inject(BankingService);
  protected readonly loading = signal(true);
  protected readonly error = signal('');

  ngOnInit(): void {
    this.banking.carregarConta().subscribe({
      next: () => this.loading.set(false),
      error: () => { this.loading.set(false); this.error.set('Não foi possível carregar os dados da conta.'); },
    });
  }
}
