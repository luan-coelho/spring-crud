package br.sst.auditoria.mapper;

import br.sst.auditoria.dto.organizacao.ConvidarMembroRequest;
import br.sst.auditoria.dto.organizacao.ConviteResponse;
import br.sst.auditoria.model.Convite;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ConviteMapper {

    @Mapping(source = "convidador.id", target = "convidadorId")
    @Mapping(source = "convidador.nome", target = "convidadorNome")
    @Mapping(source = "organizacao.id", target = "organizacaoId")
    @Mapping(source = "organizacao.nome", target = "organizacaoNome")
    ConviteResponse toResponse(Convite convite);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "expiraEm", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "organizacao", ignore = true)
    @Mapping(target = "convidador", ignore = true)
    Convite toEntity(ConvidarMembroRequest request);
}
