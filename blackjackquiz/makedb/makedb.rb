#!/usr/bin/env ruby

require_relative 'action_data'
%w{sqlite3 fileutils}.each { |mod| require mod }

module BlackJackConstants
    DB_FILE_NAME ='blackjack.sql'

    MOVES_MAP        = {
        :hit => 0,
        :dbl => 1,
        :std => 2,
        :spl => 3,
        :dfs => 4
    }

    # tables
    HARD_TABLE_NAME  = 'hard'
    SOFT_TABLE_NAME  = 'soft'
    SPLIT_TABLE_NAME = 'split'

    TABLE_COLUMNS = {
        :action            => :INT_NOT_NULL,
        :dealer_card       => :INT_NOT_NULL,
        :player_card_value => :INT_NOT_NULL,
    }
end

class BlackJackDbCreator
    extend BlackJackConstants, ActionData

    def initialize
        # make sure the file doesn't exist -> will just recreate
        FileUtils::rm(BlackJackConstants::DB_FILE_NAME, :force => true)
    end

    def open
        @db = SQLite3::Database.new(BlackJackConstants::DB_FILE_NAME)
    end

    def create_tables
        # create tables
        create_table(BlackJackConstants::HARD_TABLE_NAME, BlackJackConstants::TABLE_COLUMNS)
        create_table(BlackJackConstants::SOFT_TABLE_NAME, BlackJackConstants::TABLE_COLUMNS)
        create_table(BlackJackConstants::SPLIT_TABLE_NAME, BlackJackConstants::TABLE_COLUMNS)

        # create indices for performance
        index_columns = [:dealer_card, :player_card_value]
        create_index(BlackJackConstants::HARD_TABLE_NAME, index_columns)
        create_index(BlackJackConstants::SOFT_TABLE_NAME, index_columns)
        create_index(BlackJackConstants::SPLIT_TABLE_NAME, index_columns)
    end

    def populate_data
        columns = BlackJackConstants::TABLE_COLUMNS.keys.sort
        put_mappings_in_table(ActionData::HARD_DATA, BlackJackConstants::HARD_TABLE_NAME, columns)
        put_mappings_in_table(ActionData::SOFT_DATA, BlackJackConstants::SOFT_TABLE_NAME, columns)
        put_mappings_in_table(ActionData::SPLIT_DATA, BlackJackConstants::SPLIT_TABLE_NAME, columns)
    end

    def close
        @db.close if @db != nil
    end

    private
    def create_table(table_name, columns)
        sql = "CREATE TABLE #{table_name} ("
        sql <<= columns.map { |pair| pair * ' ' } * ', '
        sql <<= ')'
        @db.execute(sql)
    end

    def create_index(table_name, columns)
        index_sql = "CREATE INDEX #{table_name}_index ON #{table_name}(#{columns * ', '})"
        @db.execute(index_sql)
    end

    # expects columns to be in sorted order
    def put_mappings_in_table(map, table, columns)
        base_sql = "INSERT INTO #{table} (#{columns * ', '}) VALUES (?, ?, ?) "

        # HOLY SHIT... using a transaction sped the execution time by 10 fold!!!!!
        @db.transaction
        map.each { |player_value, actions|
            (0...actions.size).each { |index|
                @db.execute(base_sql, [BlackJackConstants::MOVES_MAP[actions[index]],
                                       index + 2,
                                       player_value])
            }
        }
        @db.commit
    end
end

creator = BlackJackDbCreator.new
begin
    creator.open
    creator.create_tables
    creator.populate_data
ensure
    creator.close
end

