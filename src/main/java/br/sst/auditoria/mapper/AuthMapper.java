package br.sst.auditoria.mapper;

import br.sst.auditoria.dto.auth.AuthResponse;
import br.sst.auditoria.security.CustomUserDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "token", source = "token")
    @Mapping(target = "type", constant = "Bearer")
    @Mapping(target = "id", source = "userDetails.id")
    @Mapping(target = "nome", source = "userDetails.nome")
    @Mapping(target = "email", source = "userDetails.email")
    @Mapping(target = "papel", source = "userDetails.papel")
    @Mapping(target = "imagem", source = "userDetails.imagem")
    AuthResponse toResponse(String token, CustomUserDetails userDetails);
}
