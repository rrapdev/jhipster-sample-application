package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Fabricante;
import com.mycompany.myapp.domain.Produto;
import com.mycompany.myapp.service.dto.FabricanteDTO;
import com.mycompany.myapp.service.dto.ProdutoDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Produto} and its DTO {@link ProdutoDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProdutoMapper extends EntityMapper<ProdutoDTO, Produto> {
    @Mapping(target = "fabricante", source = "fabricante", qualifiedByName = "fabricanteId")
    ProdutoDTO toDto(Produto s);

    @Named("fabricanteId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    FabricanteDTO toDtoFabricanteId(Fabricante fabricante);
}
