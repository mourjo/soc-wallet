\c postgres;

DROP DATABASE IF EXISTS soc_wallet_test_db;
CREATE DATABASE soc_wallet_test_db;
GRANT ALL PRIVILEGES ON DATABASE soc_wallet_test_db TO justin;

DROP DATABASE IF EXISTS soc_wallet_db;
CREATE DATABASE soc_wallet_db;
GRANT ALL PRIVILEGES ON DATABASE soc_wallet_db TO justin;

\c soc_wallet_db;

-- -----------------------------------------------
-- Tables
-- -----------------------------------------------

CREATE TABLE users (
   id serial PRIMARY KEY,
   name TEXT,
   email TEXT UNIQUE,
   created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE accounts (
    id serial PRIMARY KEY,
    user_id integer REFERENCES users(id),
    balance NUMERIC(12,2),
    currency TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE internal_transfers (
   id bigserial PRIMARY KEY,
   source integer REFERENCES accounts(id) NOT NULL,
   destination integer REFERENCES accounts(id) NOT NULL,
   amount NUMERIC(12,2),
   created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE external_transfers (
   id bigserial PRIMARY KEY,
   account integer REFERENCES accounts(id) NOT NULL,
   amount NUMERIC(12,2),
   source TEXT NOT NULL,
   created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- -----------------------------------------------
-- Users
-- -----------------------------------------------

INSERT INTO users (name, email) VALUES
('Peter Smith', 'peter.smith@gmail.com'),
('Janet Brown', 'janet.brown@gmail.com'),
('Pam Garcia', 'pam.garcia@gmail.com'),
('Barbara Williams', 'barbara.williams@gmail.com'),
('Jack Davis', 'jack.davis@gmail.com'),
('Collin Jones', 'collin.jones@gmail.com'),
('George Brown', 'george.brown@gmail.com');

-- -----------------------------------------------
-- Accounts
-- -----------------------------------------------

INSERT INTO accounts(user_id, balance, currency)
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='peter.smith@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='janet.brown@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='pam.garcia@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='barbara.williams@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='jack.davis@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='collin.jones@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='george.brown@gmail.com') ;

-- -----------------------------------------------
-- External transfers
-- -----------------------------------------------

INSERT INTO external_transfers (account, source, amount)
(select accounts.id as account, 'Bank Transfer', 100.50 from accounts join users on accounts.user_id=users.id where email='peter.smith@gmail.com') UNION
(select accounts.id as account, 'Bank Transfer', 500 from accounts join users on accounts.user_id=users.id where email='barbara.williams@gmail.com') UNION
(select accounts.id as account, 'Card Transfer',1100 from accounts join users on accounts.user_id=users.id where email='collin.jones@gmail.com') UNION
(select accounts.id as account, 'Bank Transfer',1100 from accounts join users on accounts.user_id=users.id where email='pam.garcia@gmail.com') UNION
(select accounts.id as account, 'Card Transfer',100 from accounts join users on accounts.user_id=users.id where email='pam.garcia@gmail.com') UNION
(select accounts.id as account, 'Bank Transfer', -50 from accounts join users on accounts.user_id=users.id where email='peter.smith@gmail.com') ;

-- -----------------------------------------------
-- Balance after external transfers
-- -----------------------------------------------

with calculated_account_balances AS (
  select act.id as account_id, sum(amount) as calculated_balance
  from external_transfers et JOIN accounts act ON act.id = et.account
  join users u on act.user_id=u.id
  group by 1
)
UPDATE accounts
SET balance = balance+calculated_account_balances.calculated_balance
FROM calculated_account_balances
WHERE id=calculated_account_balances.account_id;

-- -----------------------------------------------
-- Internal transfers
-- -----------------------------------------------

INSERT INTO internal_transfers (source, destination, amount)
(
  select
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='pam.garcia@gmail.com') as source,
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='peter.smith@gmail.com') as destination,
  20 as amount
) UNION (
  select
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='pam.garcia@gmail.com') as source,
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='barbara.williams@gmail.com') as destination,
  120 as amount
) UNION (
  select
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='peter.smith@gmail.com') as source,
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='george.brown@gmail.com') as destination,
  100 as amount
);

-- -----------------------------------------------
-- Balance after debits from internal transfers
-- -----------------------------------------------

with calculated_account_balances AS (
  select act.id as account_id, sum(amount) as calculated_balance
  from internal_transfers it JOIN accounts act ON act.id = it.source
  join users u on act.user_id=u.id
  group by 1
)
UPDATE accounts
SET balance = balance-calculated_account_balances.calculated_balance
FROM calculated_account_balances
WHERE id=calculated_account_balances.account_id;


-- -----------------------------------------------
-- Balance after credits from internal transfers
-- -----------------------------------------------

with calculated_account_balances AS (
  select act.id as account_id, sum(amount) as calculated_balance
  from internal_transfers it JOIN accounts act ON act.id = it.destination
  join users u on act.user_id=u.id
  group by 1
)
UPDATE accounts
SET balance = balance+calculated_account_balances.calculated_balance
FROM calculated_account_balances
WHERE id=calculated_account_balances.account_id;



-- ***********************************************
-- Copy the above to the test database
-- ***********************************************

\c soc_wallet_test_db;

-- -----------------------------------------------
-- Tables
-- -----------------------------------------------


CREATE TABLE users (
   id serial PRIMARY KEY,
   name TEXT,
   email TEXT UNIQUE,
   created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE accounts (
    id serial PRIMARY KEY,
    user_id integer REFERENCES users(id),
    balance NUMERIC(12,2),
    currency TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE internal_transfers (
   id bigserial PRIMARY KEY,
   source integer REFERENCES accounts(id) NOT NULL,
   destination integer REFERENCES accounts(id) NOT NULL,
   amount NUMERIC(12,2),
   created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);


CREATE TABLE external_transfers (
   id bigserial PRIMARY KEY,
   account integer REFERENCES accounts(id) NOT NULL,
   amount NUMERIC(12,2),
   source TEXT NOT NULL,
   created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- -----------------------------------------------
-- Users
-- -----------------------------------------------

INSERT INTO users (name, email) VALUES
('Peter Smith', 'peter.smith@gmail.com'),
('Janet Brown', 'janet.brown@gmail.com'),
('Pam Garcia', 'pam.garcia@gmail.com'),
('Barbara Williams', 'barbara.williams@gmail.com'),
('Jack Davis', 'jack.davis@gmail.com'),
('Collin Jones', 'collin.jones@gmail.com'),
('George Brown', 'george.brown@gmail.com');

-- -----------------------------------------------
-- Accounts
-- -----------------------------------------------

INSERT INTO accounts(user_id, balance, currency)
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='peter.smith@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='janet.brown@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='pam.garcia@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='barbara.williams@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='jack.davis@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='collin.jones@gmail.com') UNION
(select id as user_id, 0 as balance, 'EUR' as currency from users where email='george.brown@gmail.com') ;


-- -----------------------------------------------
-- External transfers
-- -----------------------------------------------

INSERT INTO external_transfers (account, source, amount)
(select accounts.id as account, 'Bank Transfer', 100.50 from accounts join users on accounts.user_id=users.id where email='peter.smith@gmail.com') UNION
(select accounts.id as account, 'Bank Transfer', 500 from accounts join users on accounts.user_id=users.id where email='barbara.williams@gmail.com') UNION
(select accounts.id as account, 'Card Transfer',1100 from accounts join users on accounts.user_id=users.id where email='collin.jones@gmail.com') UNION
(select accounts.id as account, 'Bank Transfer',1100 from accounts join users on accounts.user_id=users.id where email='pam.garcia@gmail.com') UNION
(select accounts.id as account, 'Card Transfer',100 from accounts join users on accounts.user_id=users.id where email='pam.garcia@gmail.com') UNION
(select accounts.id as account, 'Bank Transfer', -50 from accounts join users on accounts.user_id=users.id where email='peter.smith@gmail.com') ;


-- -----------------------------------------------
-- Balance after external transfers
-- -----------------------------------------------

with calculated_account_balances AS (
  select act.id as account_id, sum(amount) as calculated_balance
  from external_transfers et JOIN accounts act ON act.id = et.account
  join users u on act.user_id=u.id
  group by 1
)
UPDATE accounts
SET balance = balance+calculated_account_balances.calculated_balance
FROM calculated_account_balances
WHERE id=calculated_account_balances.account_id;


-- -----------------------------------------------
-- Internal transfers
-- -----------------------------------------------

INSERT INTO internal_transfers (source, destination, amount)
(
  select
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='pam.garcia@gmail.com') as source,
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='peter.smith@gmail.com') as destination,
  20 as amount
) UNION (
  select
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='pam.garcia@gmail.com') as source,
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='barbara.williams@gmail.com') as destination,
  120 as amount
) UNION (
  select
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='peter.smith@gmail.com') as source,
  (select acc.id from accounts acc join users u on acc.user_id=u.id where email='george.brown@gmail.com') as destination,
  100 as amount
);


-- -----------------------------------------------
-- Balance after debits from internal transfers
-- -----------------------------------------------

with calculated_account_balances AS (
  select act.id as account_id, sum(amount) as calculated_balance
  from internal_transfers it JOIN accounts act ON act.id = it.source
  join users u on act.user_id=u.id
  group by 1
)
UPDATE accounts
SET balance = balance-calculated_account_balances.calculated_balance
FROM calculated_account_balances
WHERE id=calculated_account_balances.account_id;


-- -----------------------------------------------
-- Balance after credits from internal transfers
-- -----------------------------------------------
with calculated_account_balances AS (
  select act.id as account_id, sum(amount) as calculated_balance
  from internal_transfers it JOIN accounts act ON act.id = it.destination
  join users u on act.user_id=u.id
  group by 1
)
UPDATE accounts
SET balance = balance+calculated_account_balances.calculated_balance
FROM calculated_account_balances
WHERE id=calculated_account_balances.account_id;
