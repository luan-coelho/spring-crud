package br.sst.auditoria.mapper;

import br.sst.auditoria.dto.usuario.UsuarioRequest;
import br.sst.auditoria.dto.usuario.UsuarioResponse;
import br.sst.auditoria.model.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UsuarioMapper {

    UsuarioResponse toResponse(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailVerificado", ignore = true)
    @Mapping(target = "onboardingCompleto", ignore = true)
    @Mapping(target = "banido", ignore = true)
    @Mapping(target = "motivoBanimento", ignore = true)
    @Mapping(target = "banimentoExpiraEm", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "sessoes", ignore = true)
    @Mapping(target = "contas", ignore = true)
    @Mapping(target = "membros", ignore = true)
    @Mapping(target = "convitesEnviados", ignore = true)
    @Mapping(target = "papel", defaultValue = "user")
    Usuario toEntity(UsuarioRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "emailVerificado", ignore = true)
    @Mapping(target = "onboardingCompleto", ignore = true)
    @Mapping(target = "banido", ignore = true)
    @Mapping(target = "motivoBanimento", ignore = true)
    @Mapping(target = "banimentoExpiraEm", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "sessoes", ignore = true)
    @Mapping(target = "contas", ignore = true)
    @Mapping(target = "membros", ignore = true)
    @Mapping(target = "convitesEnviados", ignore = true)
    void updateEntity(UsuarioRequest request, @MappingTarget Usuario usuario);
}
