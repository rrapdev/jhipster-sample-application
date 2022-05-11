import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IProduto, Produto } from '../produto.model';
import { ProdutoService } from '../service/produto.service';
import { IFabricante } from 'app/entities/fabricante/fabricante.model';
import { FabricanteService } from 'app/entities/fabricante/service/fabricante.service';

@Component({
  selector: 'jhi-produto-update',
  templateUrl: './produto-update.component.html',
})
export class ProdutoUpdateComponent implements OnInit {
  isSaving = false;

  fabricantesSharedCollection: IFabricante[] = [];

  editForm = this.fb.group({
    id: [],
    nome: [],
    fabricante: [],
  });

  constructor(
    protected produtoService: ProdutoService,
    protected fabricanteService: FabricanteService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ produto }) => {
      this.updateForm(produto);

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const produto = this.createFromForm();
    if (produto.id !== undefined) {
      this.subscribeToSaveResponse(this.produtoService.update(produto));
    } else {
      this.subscribeToSaveResponse(this.produtoService.create(produto));
    }
  }

  trackFabricanteById(_index: number, item: IFabricante): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProduto>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(produto: IProduto): void {
    this.editForm.patchValue({
      id: produto.id,
      nome: produto.nome,
      fabricante: produto.fabricante,
    });

    this.fabricantesSharedCollection = this.fabricanteService.addFabricanteToCollectionIfMissing(
      this.fabricantesSharedCollection,
      produto.fabricante
    );
  }

  protected loadRelationshipsOptions(): void {
    this.fabricanteService
      .query()
      .pipe(map((res: HttpResponse<IFabricante[]>) => res.body ?? []))
      .pipe(
        map((fabricantes: IFabricante[]) =>
          this.fabricanteService.addFabricanteToCollectionIfMissing(fabricantes, this.editForm.get('fabricante')!.value)
        )
      )
      .subscribe((fabricantes: IFabricante[]) => (this.fabricantesSharedCollection = fabricantes));
  }

  protected createFromForm(): IProduto {
    return {
      ...new Produto(),
      id: this.editForm.get(['id'])!.value,
      nome: this.editForm.get(['nome'])!.value,
      fabricante: this.editForm.get(['fabricante'])!.value,
    };
  }
}
