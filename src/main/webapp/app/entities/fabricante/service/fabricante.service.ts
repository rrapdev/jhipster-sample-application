import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFabricante, getFabricanteIdentifier } from '../fabricante.model';

export type EntityResponseType = HttpResponse<IFabricante>;
export type EntityArrayResponseType = HttpResponse<IFabricante[]>;

@Injectable({ providedIn: 'root' })
export class FabricanteService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/fabricantes');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(fabricante: IFabricante): Observable<EntityResponseType> {
    return this.http.post<IFabricante>(this.resourceUrl, fabricante, { observe: 'response' });
  }

  update(fabricante: IFabricante): Observable<EntityResponseType> {
    return this.http.put<IFabricante>(`${this.resourceUrl}/${getFabricanteIdentifier(fabricante) as number}`, fabricante, {
      observe: 'response',
    });
  }

  partialUpdate(fabricante: IFabricante): Observable<EntityResponseType> {
    return this.http.patch<IFabricante>(`${this.resourceUrl}/${getFabricanteIdentifier(fabricante) as number}`, fabricante, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFabricante>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFabricante[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addFabricanteToCollectionIfMissing(
    fabricanteCollection: IFabricante[],
    ...fabricantesToCheck: (IFabricante | null | undefined)[]
  ): IFabricante[] {
    const fabricantes: IFabricante[] = fabricantesToCheck.filter(isPresent);
    if (fabricantes.length > 0) {
      const fabricanteCollectionIdentifiers = fabricanteCollection.map(fabricanteItem => getFabricanteIdentifier(fabricanteItem)!);
      const fabricantesToAdd = fabricantes.filter(fabricanteItem => {
        const fabricanteIdentifier = getFabricanteIdentifier(fabricanteItem);
        if (fabricanteIdentifier == null || fabricanteCollectionIdentifiers.includes(fabricanteIdentifier)) {
          return false;
        }
        fabricanteCollectionIdentifiers.push(fabricanteIdentifier);
        return true;
      });
      return [...fabricantesToAdd, ...fabricanteCollection];
    }
    return fabricanteCollection;
  }
}
