package br.com.minhasfinancas.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.minhasfinancas.dto.UsuarioDTO;
import br.com.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.minhasfinancas.exception.RegraNegocioException;
import br.com.minhasfinancas.model.Usuario;
import br.com.minhasfinancas.service.LancamentoService;
import br.com.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping(path = "/api/usuarios")
public class UsuarioController {
	
	@Autowired
	private UsuarioService usuarioService;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@PostMapping(path = "/salvarUsuario")
	public ResponseEntity<Object> salvarUsuario(@RequestBody UsuarioDTO usuarioDTO) {
		Usuario usuario = Usuario
				.builder()
				.nomeUsuario(usuarioDTO.getNomeUsuario())
				.emailUsuario(usuarioDTO.getEmailUsuario())
				.senhaUsuario(usuarioDTO.getSenhaUsuario())
				.build();
		
		try {
			Usuario usuarioSalvo = usuarioService.salvarUsuario(usuario);
			return new ResponseEntity<Object>(usuarioSalvo, HttpStatus.CREATED);
		}catch(RegraNegocioException e){
			return ResponseEntity.badRequest().body(e);
		}
	}
	
	@PostMapping(path = "/autenticar")
	public ResponseEntity<Object> autenticarUsuario(@RequestBody UsuarioDTO usuarioDTO){
		try {
			Usuario usuarioAutenticado = usuarioService.autenticar(usuarioDTO.getEmailUsuario(), usuarioDTO.getSenhaUsuario());
			return ResponseEntity.ok(usuarioAutenticado);
		} catch (ErroAutenticacaoException e) {
			return ResponseEntity.badRequest().body(e);
		}
	}
	
	@GetMapping(path = "/{idUsuario}/saldo")
	public ResponseEntity<Object> obterSaldo(@PathVariable Long idUsuario){
		Optional<Usuario> usuario = usuarioService.buscarPorId(idUsuario);
		
		if(!usuario.isPresent()) {
			return new ResponseEntity<Object>(HttpStatus.NOT_FOUND);
		}
		
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(idUsuario);
		return ResponseEntity.ok(saldo);
		
	}
}
