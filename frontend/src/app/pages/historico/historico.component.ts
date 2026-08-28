import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { BankingService } from '../../core/banking.service';

@Component({
  selector: 'app-historico',
  imports: [CurrencyPipe, DatePipe],
  templateUrl: './historico.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class HistoricoComponent {
  protected readonly banking = inject(BankingService);
}
