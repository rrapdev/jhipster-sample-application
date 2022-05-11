import { IFabricante } from 'app/entities/fabricante/fabricante.model';

export interface IProduto {
  id?: number;
  nome?: string | null;
  fabricante?: IFabricante | null;
}

export class Produto implements IProduto {
  constructor(public id?: number, public nome?: string | null, public fabricante?: IFabricante | null) {}
}

export function getProdutoIdentifier(produto: IProduto): number | undefined {
  return produto.id;
}
