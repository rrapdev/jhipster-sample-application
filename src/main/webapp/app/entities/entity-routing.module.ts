import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'produto',
        data: { pageTitle: 'jhipsterSampleApplicationApp.produto.home.title' },
        loadChildren: () => import('./produto/produto.module').then(m => m.ProdutoModule),
      },
      {
        path: 'fabricante',
        data: { pageTitle: 'jhipsterSampleApplicationApp.fabricante.home.title' },
        loadChildren: () => import('./fabricante/fabricante.module').then(m => m.FabricanteModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
