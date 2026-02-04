package br.sst.auditoria.mapper;

import br.sst.auditoria.dto.organizacao.AtualizarPapelRequest;
import br.sst.auditoria.dto.organizacao.CriarPapelRequest;
import br.sst.auditoria.dto.organizacao.PapelOrganizacaoResponse;
import br.sst.auditoria.model.PapelOrganizacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PapelOrganizacaoMapper {

    @Mapping(source = "organizacao.id", target = "organizacaoId")
    PapelOrganizacaoResponse toResponse(PapelOrganizacao papelOrganizacao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "organizacao", ignore = true)
    PapelOrganizacao toEntity(CriarPapelRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "organizacao", ignore = true)
    @Mapping(target = "papel", source = "novoNome")
    void updateEntity(AtualizarPapelRequest request, @MappingTarget PapelOrganizacao papelOrganizacao);
}
