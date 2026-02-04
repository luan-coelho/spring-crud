package br.sst.auditoria.mapper;

import br.sst.auditoria.dto.organizacao.AdicionarMembroRequest;
import br.sst.auditoria.dto.organizacao.MembroResponse;
import br.sst.auditoria.model.Membro;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MembroMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nome", target = "usuarioNome")
    @Mapping(source = "usuario.email", target = "usuarioEmail")
    @Mapping(source = "organizacao.id", target = "organizacaoId")
    MembroResponse toResponse(Membro membro);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "organizacao", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Membro toEntity(AdicionarMembroRequest request);
}
