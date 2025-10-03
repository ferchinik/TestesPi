
-- Create users
INSERT INTO usuarios (username, password) VALUES
    ('admin', '$2a$10$Doit7Pk8TgzuHbVcz3pD7O6/5H4vCcDodoONv1cgPlgrM8Be8WCqu');

INSERT INTO usuarios (username, password) VALUES
    ('user', '$2a$12$iSyrL4gAAm4IHjfQJPOeluSBLUdlt95cdHr5GBbtCB02zR6tryTea');

-- Insert roles
INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO user_roles (user_id, role) VALUES (1, 'ROLE_USER');
INSERT INTO user_roles (user_id, role) VALUES (2, 'ROLE_USER');

-- Insert Categories
INSERT INTO categorias (nome) VALUES ('Anéis');
INSERT INTO categorias (nome) VALUES ('Colares');
INSERT INTO categorias (nome) VALUES ('Pulseiras');
INSERT INTO categorias (nome) VALUES ('Brincos');

-- Insert Suppliers
INSERT INTO fornecedores (nome) VALUES ('Ouro Nobre Ltda');
INSERT INTO fornecedores (nome) VALUES ('Prata Fina SA');
INSERT INTO fornecedores (nome) VALUES ('Gemas Raras Com');

-- Insert Clients
INSERT INTO clientes (nome, email) VALUES ('Ana Silva', 'ana.silva@email.com');
INSERT INTO clientes (nome, email) VALUES ('Fernanda Santos', 'fernandasantos@gmail.com');
INSERT INTO clientes (nome, email) VALUES ('Carla Dias', 'carla.d@email.org');

-- Insert Jewelry
INSERT INTO joias (nome, preco, categoria_id, fornecedor_id) VALUES ('Anel Solitário Ouro', 1500.00, 1, 1);
INSERT INTO joias (nome, preco, categoria_id, fornecedor_id) VALUES ('Colar de Pérolas', 850.50, 2, 2);
INSERT INTO joias (nome, preco, categoria_id, fornecedor_id) VALUES ('Pulseira Infinito Prata', 350.00, 3, 2);
INSERT INTO joias (nome, preco, categoria_id, fornecedor_id) VALUES ('Brinco Esmeralda Ouro', 2200.75, 4, 1);
INSERT INTO joias (nome, preco, categoria_id, fornecedor_id) VALUES ('Anel Aparador Prata', 280.00, 1, 2);

-- Insert Orders
INSERT INTO pedidos (data_pedido, cliente_id) VALUES ('2025-04-08', 1); -- Order for Ana Silva
INSERT INTO pedidos (data_pedido, cliente_id) VALUES ('2025-04-09', 2); -- Order for Fernanda Santos
INSERT INTO pedidos (data_pedido, cliente_id) VALUES ('2025-04-10', 1); -- Another order for Ana Silva

-- Insert Order-Jewelry relationships
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (1, 1); -- Order 1 with 'Anel Solitário Ouro'
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (1, 4); -- Order 1 with 'Brinco Esmeralda Ouro'
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (2, 2); -- Order 2 with 'Colar de Pérolas'
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (2, 3); -- Order 2 with 'Pulseira Infinito Prata'
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (3, 5); -- Order 3 with 'Anel Aparador Prata'
INSERT INTO pedido_joia (pedido_id, joia_id) VALUES (3, 3); -- Order 3 with 'Pulseira Infinito Prata'