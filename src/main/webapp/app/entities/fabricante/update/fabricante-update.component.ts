import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IFabricante, Fabricante } from '../fabricante.model';
import { FabricanteService } from '../service/fabricante.service';

@Component({
  selector: 'jhi-fabricante-update',
  templateUrl: './fabricante-update.component.html',
})
export class FabricanteUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    nome: [],
  });

  constructor(protected fabricanteService: FabricanteService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ fabricante }) => {
      this.updateForm(fabricante);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const fabricante = this.createFromForm();
    if (fabricante.id !== undefined) {
      this.subscribeToSaveResponse(this.fabricanteService.update(fabricante));
    } else {
      this.subscribeToSaveResponse(this.fabricanteService.create(fabricante));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFabricante>>): void {
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

  protected updateForm(fabricante: IFabricante): void {
    this.editForm.patchValue({
      id: fabricante.id,
      nome: fabricante.nome,
    });
  }

  protected createFromForm(): IFabricante {
    return {
      ...new Fabricante(),
      id: this.editForm.get(['id'])!.value,
      nome: this.editForm.get(['nome'])!.value,
    };
  }
}
