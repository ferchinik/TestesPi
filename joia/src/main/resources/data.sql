-- src/main/resources/data.sql (versão revisada)

-- Categorias
INSERT INTO categorias (nome) VALUES ('Anéis');
INSERT INTO categorias (nome) VALUES ('Colares');
INSERT INTO categorias (nome) VALUES ('Pulseiras');
INSERT INTO categorias (nome) VALUES ('Brincos');

-- Fornecedores
INSERT INTO fornecedores (nome) VALUES ('Ouro Nobre Ltda');
INSERT INTO fornecedores (nome) VALUES ('Prata Fina SA');
INSERT INTO fornecedores (nome) VALUES ('Gemas Raras Com');

-- Clientes
INSERT INTO clientes (nome, email) VALUES ('Ana Silva', 'ana.silva@email.com');
INSERT INTO clientes (nome, email) VALUES ('Fernanda Santos', 'fernandasantos@gmail.com');
INSERT INTO clientes (nome, email) VALUES ('Carla Dias', 'carla.d@email.org');

-- Joias
INSERT INTO joias (nome, preco, categoria_id, fornecedor_id) VALUES ('Anel Solitário Ouro', 1500.00, 1, 1);
INSERT INTO joias (nome, preco, categoria_id, fornecedor_id) VALUES ('Colar de Pérolas', 850.50, 2, 2);
INSERT INTO joias (nome, preco, categoria_id, fornecedor_id) VALUES ('Pulseira Infinito Prata', 350.00, 3, 2);
INSERT INTO joias (nome, preco, categoria_id, fornecedor_id) VALUES ('Brinco Esmeralda Ouro', 2200.75, 4, 1);
INSERT INTO joias (nome, preco, categoria_id, fornecedor_id) VALUES ('Anel Aparador Prata', 280.00, 1, 2);

-- Pedidos
INSERT INTO pedidos (data_pedido, cliente_id) VALUES ('2025-04-08', 1);
INSERT INTO pedidos (data_pedido, cliente_id) VALUES ('2025-04-09', 2);
INSERT INTO pedidos (data_pedido, cliente_id) VALUES ('2025-04-10', 1);

-- Pedido_Joia (Tabela de Junção Many-to-Many - MANTÉM OS IDs)
-- Assumindo que os IDs auto-gerados para pedidos e joias serão 1, 2, 3...
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (1, 1); -- Pedido 1 com Joia 1
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (1, 4); -- Pedido 1 com Joia 4
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (2, 2); -- Pedido 2 com Joia 2
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (2, 3); -- Pedido 2 com Joia 3
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (3, 5); -- Pedido 3 com Joia 5
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (3, 3); -- Pedido 3 com Joia 3