import { IProduto } from 'app/entities/produto/produto.model';

export interface IFabricante {
  id?: number;
  nome?: string | null;
  produtos?: IProduto[] | null;
}

export class Fabricante implements IFabricante {
  constructor(public id?: number, public nome?: string | null, public produtos?: IProduto[] | null) {}
}

export function getFabricanteIdentifier(fabricante: IFabricante): number | undefined {
  return fabricante.id;
}
