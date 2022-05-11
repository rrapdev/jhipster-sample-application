package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Fabricante;
import com.mycompany.myapp.service.dto.FabricanteDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Fabricante} and its DTO {@link FabricanteDTO}.
 */
@Mapper(componentModel = "spring")
public interface FabricanteMapper extends EntityMapper<FabricanteDTO, Fabricante> {}
