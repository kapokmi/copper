--schema
CREATE SCHEMA IF NOT EXISTS bitmex;

--sequence
DROP SEQUENCE IF EXISTS bitmex.wallet_id_seq;

CREATE SEQUENCE bitmex.wallet_id_seq;

--table
DROP TABLE IF EXISTS bitmex.wallets;

CREATE TABLE bitmex.wallets (
  wallet_id BIGINT PRIMARY KEY,
  account_id BIGINT NOT NULL,
  currency VARCHAR(3) NOT NULL,
  balance NUMERIC NOT NULL,
  reserved_funds NUMERIC NOT NULL,
  created TIMESTAMP NOT NULL,
  last_updated TIMESTAMP NOT NULL
);

 ALTER TABLE bitmex.wallets ADD CONSTRAINT unique_currency_wallet UNIQUE(account_id, currency);