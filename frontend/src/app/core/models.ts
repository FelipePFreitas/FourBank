export interface AuthTokenResponse {
  tokenType: string;
  accessToken: string;
  expiresInMillis: number;
}

export interface Conta {
  id: string;
  agencia: string;
  numeroConta: string;
  saldo: number;
  clienteId: string;
}

export type TipoOperacao = 'PIX' | 'DEPOSITO' | 'SAQUE' | 'TRANSFERENCIA';

export interface Transacao {
  id: string;
  tipoTransacao: TipoOperacao | string;
  valor: number;
  descricao?: string;
  criadoEm: string;
  contaOrigemId?: string;
  contaDestinoId?: string;
}

export interface TransferenciaRequest {
  nome: string;
  documento: string;
  banco: string;
  agencia: string;
  conta: string;
  tipoConta: 'CC' | 'CP';
  valor: number;
  agendadaPara: string | null;
}

export interface EnderecoRequest {
  endereco: string;
  numero: string;
  cep: string;
  bairro: string;
  cidade: string;
  uf: string;
}

export interface UsuarioRequest {
  login: string;
  senha: string;
}

export interface CadastroPFRequest {
  nome: string;
  dataNascimento: string;
  cpf: string;
  email: string;
  telefone: string;
  endereco: EnderecoRequest;
  usuario: UsuarioRequest;
}

export interface CadastroPJRequest {
  nomeRazaoSocial: string;
  nomeFantasia: string;
  dataFundacao: string | null;
  faturamentoAnual: number;
  documento: string;
  email: string;
  telefone: string;
  endereco: EnderecoRequest;
  usuario: UsuarioRequest;
}
