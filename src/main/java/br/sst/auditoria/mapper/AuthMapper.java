package br.sst.auditoria.mapper;

import br.sst.auditoria.dto.auth.AuthResponse;
import br.sst.auditoria.security.CustomUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AuthMapper {

    @Mapping(target = "token", source = "token")
    @Mapping(target = "type", constant = "Bearer")
    @Mapping(target = "id", source = "userDetails.id")
    @Mapping(target = "nome", source = "userDetails.nome")
    @Mapping(target = "email", source = "userDetails.email")
    @Mapping(target = "papel", source = "userDetails.papel")
    @Mapping(target = "imagem", source = "userDetails.imagem")
    AuthResponse toResponse(String token, CustomUserDetails userDetails);

    @Mapping(target = "token", ignore = true)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "nome", source = "nome")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "papel", source = "papel")
    @Mapping(target = "imagem", source = "imagem")
    AuthResponse toResponse(CustomUserDetails userDetails);
}
