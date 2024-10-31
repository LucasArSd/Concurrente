/*solución punto A*/

chan PedidoAtencion(idCliente)
chan AdondeIr[N](idCabina);

chan Pagos(idCliente)
chan TicketFactura[N](unTicket)

process Empleado{
	int idCliente, idCabina;
	text resp;
	text ticket
	pila cabinas[10]{1..10};

	while true(){
		if (!cabinas.isEmpty()){ //Verifico si hay cabina libre
			if (not empty(PedidoAtencion)){
				receive PedidoAtencion(idCliente);
				idCabina = cabinas.pop(); //desapilo la cabina
				send AdondeIr[idCliente](idCabina);
			}
		}
		
		if(not empty(Pagos)){ //Si hay que cobrar a clientes el uso de cabinas
			receive Pagos(idCliente, idCabina);
			cabinas.push(idCabina);
			Cobrar(idCliente, ticket)
			send TicketFactura[idCliente](ticket)
		}
	}
}

process Cliente[id: 0..N-1]{
	int idCabina;
	text ticket;
	//avisa llegada a empleado
	send PedidoAtencion(id);
	//Empleado le indica a donde ir
	receive AdondeIr[id](idCabina);
	//utiliza cabina
	utilizarCabinaTelefonica(idCabina);
	//envia pago a empleado
	send Pagos(id);
	//espera a recibir el ticket
	receive TicketFactura[id](ticket);
}

/*solución punto B*/

//Lo que se me ocurrio es poner al principio del process empleado, un while de not Empty(Pagos)
