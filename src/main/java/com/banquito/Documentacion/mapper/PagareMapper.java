package com.banquito.Documentacion.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.banquito.Documentacion.dto.PagareDTO;
import com.banquito.Documentacion.dto.PagareCreateDTO;
import com.banquito.Documentacion.dto.PagareUpdateDTO;
import com.banquito.Documentacion.model.Pagare;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PagareMapper {

    // CREAR: solo se mapean campos del DTO de creación. El ID lo ignora (auto generado).
    @Mapping(target = "idPagare", ignore = true)
    @Mapping(target = "version", ignore = true)
    Pagare toEntity(PagareCreateDTO dto);

    // ACTUALIZAR: solo mapea los campos actualizables
    @Mapping(target = "idPagare", ignore = true)
    @Mapping(target = "idContratoCredito", ignore = true)
    @Mapping(target = "version", ignore = true)
    void updateEntity(@MappingTarget Pagare entity, PagareUpdateDTO dto);

    // Entidad a DTO de respuesta
    PagareDTO toDto(Pagare entity);

    // Lista de entidades a lista de DTOs
    List<PagareDTO> toDtoList(List<Pagare> entities);
}