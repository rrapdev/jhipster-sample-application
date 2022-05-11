package com.mycompany.myapp.service.impl;

import com.mycompany.myapp.domain.Produto;
import com.mycompany.myapp.repository.ProdutoRepository;
import com.mycompany.myapp.service.ProdutoService;
import com.mycompany.myapp.service.dto.ProdutoDTO;
import com.mycompany.myapp.service.mapper.ProdutoMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Produto}.
 */
@Service
@Transactional
public class ProdutoServiceImpl implements ProdutoService {

    private final Logger log = LoggerFactory.getLogger(ProdutoServiceImpl.class);

    private final ProdutoRepository produtoRepository;

    private final ProdutoMapper produtoMapper;

    public ProdutoServiceImpl(ProdutoRepository produtoRepository, ProdutoMapper produtoMapper) {
        this.produtoRepository = produtoRepository;
        this.produtoMapper = produtoMapper;
    }

    @Override
    public ProdutoDTO save(ProdutoDTO produtoDTO) {
        log.debug("Request to save Produto : {}", produtoDTO);
        Produto produto = produtoMapper.toEntity(produtoDTO);
        produto = produtoRepository.save(produto);
        return produtoMapper.toDto(produto);
    }

    @Override
    public ProdutoDTO update(ProdutoDTO produtoDTO) {
        log.debug("Request to save Produto : {}", produtoDTO);
        Produto produto = produtoMapper.toEntity(produtoDTO);
        produto = produtoRepository.save(produto);
        return produtoMapper.toDto(produto);
    }

    @Override
    public Optional<ProdutoDTO> partialUpdate(ProdutoDTO produtoDTO) {
        log.debug("Request to partially update Produto : {}", produtoDTO);

        return produtoRepository
            .findById(produtoDTO.getId())
            .map(existingProduto -> {
                produtoMapper.partialUpdate(existingProduto, produtoDTO);

                return existingProduto;
            })
            .map(produtoRepository::save)
            .map(produtoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProdutoDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Produtos");
        return produtoRepository.findAll(pageable).map(produtoMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProdutoDTO> findOne(Long id) {
        log.debug("Request to get Produto : {}", id);
        return produtoRepository.findById(id).map(produtoMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Produto : {}", id);
        produtoRepository.deleteById(id);
    }
}
