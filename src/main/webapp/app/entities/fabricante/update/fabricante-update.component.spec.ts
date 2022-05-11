import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { FabricanteService } from '../service/fabricante.service';
import { IFabricante, Fabricante } from '../fabricante.model';

import { FabricanteUpdateComponent } from './fabricante-update.component';

describe('Fabricante Management Update Component', () => {
  let comp: FabricanteUpdateComponent;
  let fixture: ComponentFixture<FabricanteUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let fabricanteService: FabricanteService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [FabricanteUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(FabricanteUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(FabricanteUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fabricanteService = TestBed.inject(FabricanteService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const fabricante: IFabricante = { id: 456 };

      activatedRoute.data = of({ fabricante });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(fabricante));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Fabricante>>();
      const fabricante = { id: 123 };
      jest.spyOn(fabricanteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fabricante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fabricante }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(fabricanteService.update).toHaveBeenCalledWith(fabricante);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Fabricante>>();
      const fabricante = new Fabricante();
      jest.spyOn(fabricanteService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fabricante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: fabricante }));
      saveSubject.complete();

      // THEN
      expect(fabricanteService.create).toHaveBeenCalledWith(fabricante);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Fabricante>>();
      const fabricante = { id: 123 };
      jest.spyOn(fabricanteService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ fabricante });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(fabricanteService.update).toHaveBeenCalledWith(fabricante);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
