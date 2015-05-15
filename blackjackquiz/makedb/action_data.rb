class MapBuilder
    attr_reader :map

    def initialize(map)
        @map = map
    end

    def multi_map(keys, value)
        [*keys].each { |key|
            map[key] = value
        }
        self
    end
end

module ActionData
    :HARD_HAND
    :SOFT_HAND
    :SPLIT_HAND

    # mappings are from player total value to map of dealer hand to action
    HARD_DATA = MapBuilder.new(Hash.new({}))
                    .multi_map((4..8),   [:hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                    .multi_map(9,        [:hit, :dbl, :dbl, :dbl, :dbl, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                    .multi_map(10,       [:hit, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :hit, :hit, :hit, :hit, :hit])
                    .multi_map(11,       [:dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl])
                    .multi_map(12,       [:hit, :hit, :std, :std, :std, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                    .multi_map((13..16), [:std, :std, :std, :std, :std, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                    .multi_map((17..20), [:std, :std, :std, :std, :std, :std, :std, :std, :std, :std, :std, :std, :std])
                    .map

    SOFT_DATA = MapBuilder.new(Hash.new({}))
                    .multi_map((13..14), [:hit, :hit, :hit, :dbl, :dbl, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                    .multi_map((15..16), [:hit, :hit, :dbl, :dbl, :dbl, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                    .multi_map(17      , [:hit, :dbl, :dbl, :dbl, :dbl, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                    .multi_map(18      , [:dbl, :dbl, :dbl, :dbl, :dbl, :std, :std, :hit, :hit, :hit, :hit, :hit, :hit])
                    .multi_map(19      , [:std, :std, :std, :std, :dbl, :std, :std, :std, :std, :std, :std, :std, :std])
                    .multi_map((20..21), [:std, :std, :std, :std, :std, :std, :std, :std, :std, :std, :std, :std, :std])
                    .map

    SPLIT_DATA = MapBuilder.new(Hash.new({}))
                     .multi_map((2..3)  , [:dfs, :dfs, :spl, :spl, :spl, :spl, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                     .multi_map(4       , [:hit, :hit, :hit, :dfs, :dfs, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                     .multi_map(5       , [:hit, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :dbl, :hit, :hit, :hit, :hit, :hit]) # 5 is the same as hard 10
                     .multi_map(6       , [:dfs, :spl, :spl, :spl, :spl, :hit, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                     .multi_map(7       , [:spl, :spl, :spl, :spl, :spl, :spl, :hit, :hit, :hit, :hit, :hit, :hit, :hit])
                     .multi_map(8       , [:spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl])
                     .multi_map(9       , [:spl, :spl, :spl, :spl, :spl, :std, :spl, :spl, :std, :std, :std, :std, :std])
                     .multi_map(10      , [:std, :std, :std, :std, :std, :std, :std, :std, :std, :std, :std, :std, :std]) # 10 is same as hard 20
                     .multi_map(11      , [:spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl, :spl])
                     .map
end
