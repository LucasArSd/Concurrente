chan canalCliente(idCliente);             // Canal para recibir una nueva solicitud de cliente
chan canalClienteTermino(idCajero);       // Canal para notificar que un cajero terminó con un cliente
chan hayPedido();                             // Canal para indicar que hay un nuevo pedido

chan colaMasCorta[P](idCajero);           // Canal para enviar el cajero con la menor fila al cliente
chan canalComprobante[P](comprobante);   // Canal para enviar el comprobante al cliente

chan canalCaja[5](idCliente);             // Canal para que el cajero reciba un cliente

process Cliente[id: 0..P-1] {
    int idCajero;
    text comprobante;

    send canalCliente(id);
    send hayPedido(); //Envío solicitud al coordinador

    // Espero recibir el cajero con la menor fila
    receive colaMasCorta[id](idCajero);

    // Aviso al cajero seleccionado
    send canalCaja[idCajero](id);

    // Recibo el comprobante del cajero
    receive canalComprobante[id](comprobante);

    // Notifico al coordinador que el cajero ha terminado con el cliente
    send canalClienteTermino(idCajero);
}

// Proceso Cajero
process Cajero[id: 0..5-1] {
    int idCliente;
    text comprobante;

    while (true) {
        // Espero que el cliente me envíe su solicitud de atención
        receive canalCaja[id](idCliente);

        // Genero el comprobante para el cliente
        generarComprobante(idCliente);
        
        // Envío el comprobante al cliente
        send canalComprobante[idCliente](comprobante);
    }
}

// Proceso Coordinador
process Coordinador {
    int idCliente;
    int idCajero;
    Array cajasCantidad[5]=[5](0)          // Array para llevar el conteo de clientes en cada caja
    int menorFila;

    while (true) {

        // Espero que haya un nuevo pedido
        receive hayPedido();

        // Si algún cliente terminó de utilizar el cajero, actualizo el contador
        if (not empty(canalClienteTermino)) {
            receive canalClienteTermino(idCajero);
            cajasCantidad[idCajero]--;
        }
        // Si hay un cliente esperando, lo atiendo y le doy la fila más corta(aplico el min/max de toda la vida)
        else if (not empty(canalCliente)) {
            receive canalCliente(idCliente);
            // Encuentro el cajero con la menor cantidad de clientes en espera
            menorFila = 0;
            for (int i = 1; i < 5; i++) {
                if (cajasCantidad[i] < cajasCantidad[menorFila]) {
                    menorFila = i;
                }
            }

            // Asigno al cliente la caja con menor fila y actualizo el contador
            send colaMasCorta[idCliente](menorFila);
            cajasCantidad[menorFila]++;
        }
    }
}
