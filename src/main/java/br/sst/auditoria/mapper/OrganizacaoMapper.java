package br.sst.auditoria.mapper;

import br.sst.auditoria.dto.organizacao.AtualizarOrganizacaoRequest;
import br.sst.auditoria.dto.organizacao.CriarOrganizacaoRequest;
import br.sst.auditoria.dto.organizacao.OrganizacaoCompletaResponse;
import br.sst.auditoria.dto.organizacao.OrganizacaoResponse;
import br.sst.auditoria.dto.organizacao.MembroResponse;
import br.sst.auditoria.dto.organizacao.ConviteResponse;
import br.sst.auditoria.model.Organizacao;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface OrganizacaoMapper {

    OrganizacaoResponse toResponse(Organizacao organizacao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "membros", ignore = true)
    @Mapping(target = "convites", ignore = true)
    @Mapping(target = "papeis", ignore = true)
    @Mapping(target = "enderecos", ignore = true)
    @Mapping(target = "empresas", ignore = true)
    @Mapping(target = "unidades", ignore = true)
    @Mapping(target = "setores", ignore = true)
    @Mapping(target = "cargos", ignore = true)
    @Mapping(target = "funcionarios", ignore = true)
    Organizacao toEntity(CriarOrganizacaoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "membros", ignore = true)
    @Mapping(target = "convites", ignore = true)
    @Mapping(target = "papeis", ignore = true)
    @Mapping(target = "enderecos", ignore = true)
    @Mapping(target = "empresas", ignore = true)
    @Mapping(target = "unidades", ignore = true)
    @Mapping(target = "setores", ignore = true)
    @Mapping(target = "cargos", ignore = true)
    @Mapping(target = "funcionarios", ignore = true)
    void updateEntity(AtualizarOrganizacaoRequest request, @MappingTarget Organizacao organizacao);

    @Mapping(target = "membros", source = "membrosResponse")
    @Mapping(target = "convites", source = "convitesResponse")
    @Mapping(target = "id", source = "organizacao.id")
    @Mapping(target = "nome", source = "organizacao.nome")
    @Mapping(target = "slug", source = "organizacao.slug")
    @Mapping(target = "logo", source = "organizacao.logo")
    @Mapping(target = "metadados", source = "organizacao.metadados")
    @Mapping(target = "criadoEm", source = "organizacao.criadoEm")
    OrganizacaoCompletaResponse toCompletaResponse(Organizacao organizacao, List<MembroResponse> membrosResponse, List<ConviteResponse> convitesResponse);
}
