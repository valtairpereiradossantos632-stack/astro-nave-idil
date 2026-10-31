package com.example.data.model

object GameDefinitions {

    val DEFAULT_GENERATORS = listOf(
        GeneratorDef(
            id = "gen_probe",
            name = "Sonda Orbital",
            baseCost = 15.0,
            baseCps = 0.5,
            iconEmoji = "🛰️",
            description = "Coleta micro-poeira estelar em órbita baixa."
        ),
        GeneratorDef(
            id = "gen_harvester",
            name = "Minerador de Asteroides",
            baseCost = 100.0,
            baseCps = 4.0,
            iconEmoji = "⛏️",
            description = "Extrai cristais valiosos do cinturão de asteroides."
        ),
        GeneratorDef(
            id = "gen_station",
            name = "Coletor de Plasma Solar",
            baseCost = 1100.0,
            baseCps = 32.0,
            iconEmoji = "☀️",
            description = "Condensa labaredas solares em fótons concentrados."
        ),
        GeneratorDef(
            id = "gen_quantum",
            name = "Reator Quântico",
            baseCost = 12000.0,
            baseCps = 260.0,
            iconEmoji = "⚛️",
            description = "Gera oscilações subatômicas de energia pura."
        ),
        GeneratorDef(
            id = "gen_antimatter",
            name = "Usina de Antimatéria",
            baseCost = 130000.0,
            baseCps = 1400.0,
            iconEmoji = "⚡",
            description = "Converte matéria escura em explosões limpas de energia."
        ),
        GeneratorDef(
            id = "gen_dyson",
            name = "Enxame de Dyson",
            baseCost = 1400000.0,
            baseCps = 8200.0,
            iconEmoji = "🪐",
            description = "Megaestrutura orbital que envolve uma estrela inteira."
        ),
        GeneratorDef(
            id = "gen_rift",
            name = "Fenda Dimensional",
            baseCost = 20000000.0,
            baseCps = 55000.0,
            iconEmoji = "🌀",
            description = "Canaliza fluxo de energia de universos paralelos."
        ),
        GeneratorDef(
            id = "gen_singularity",
            name = "Singularidade Controlada",
            baseCost = 350000000.0,
            baseCps = 420000.0,
            iconEmoji = "🕳️",
            description = "Um buraco negro aprisionado gerando radiação Hawking contínua."
        )
    )

    val DEFAULT_PRESTIGE_UPGRADES = listOf(
        PrestigeUpgradeDef(
            id = "p_resonance",
            name = "Ressonância Cósmica",
            description = "+25% de produção global de energia por nível.",
            baseDarkMatterCost = 1L,
            costMultiplier = 1.6,
            maxLevel = 25,
            iconEmoji = "🔮"
        ),
        PrestigeUpgradeDef(
            id = "p_efficiency",
            name = "Condensação Espacial",
            description = "-5% no custo de todos os geradores automáticos.",
            baseDarkMatterCost = 2L,
            costMultiplier = 1.8,
            maxLevel = 10,
            iconEmoji = "💎"
        ),
        PrestigeUpgradeDef(
            id = "p_crit_power",
            name = "Supernova Manual",
            description = "+50% de dano e energia nos cliques críticos.",
            baseDarkMatterCost = 1L,
            costMultiplier = 1.7,
            maxLevel = 20,
            iconEmoji = "💥"
        ),
        PrestigeUpgradeDef(
            id = "p_offline_vault",
            name = "Câmara Criogênica",
            description = "+2 horas no limite máximo de acumulação offline.",
            baseDarkMatterCost = 3L,
            costMultiplier = 2.0,
            maxLevel = 8,
            iconEmoji = "⏳"
        ),
        PrestigeUpgradeDef(
            id = "p_meteor_luck",
            name = "Imã de Cometas",
            description = "+15% de chance e frequência de cometas dourados.",
            baseDarkMatterCost = 2L,
            costMultiplier = 1.9,
            maxLevel = 15,
            iconEmoji = "🌠"
        )
    )

    val ALL_COSMETICS = listOf(
        // Skins
        CosmeticItem("skin_neon", "Cristal Neon Ciano", CosmeticType.CORE_SKIN, Rarity.COMMON, "O clássico prisma cósmico luminescente.", "💎"),
        CosmeticItem("skin_ruby", "Núcleo de Plasma Rubro", CosmeticType.CORE_SKIN, Rarity.RARE, "Irradia calor estelar incandescente.", "🔥"),
        CosmeticItem("skin_void", "Buraco Negro Estelar", CosmeticType.CORE_SKIN, Rarity.EPIC, "Distorce o tecido do espaço ao seu redor.", "🕳️"),
        CosmeticItem("skin_divine", "Prisma Cósmico Divino", CosmeticType.CORE_SKIN, Rarity.LEGENDARY, "Forjado no nascimento do universo primordial.", "✨"),
        // Trails
        CosmeticItem("trail_stars", "Poeira Estelar Azul", CosmeticType.TAP_TRAIL, Rarity.COMMON, "Partículas celestes suaves a cada clique.", "✨"),
        CosmeticItem("trail_fire", "Labaredas Solares", CosmeticType.TAP_TRAIL, Rarity.RARE, "Faíscas de plasma em cascata.", "⚡"),
        CosmeticItem("trail_aurora", "Aurora Boreal Quântica", CosmeticType.TAP_TRAIL, Rarity.EPIC, "Ondas prismáticas psicodélicas.", "🌈"),
        CosmeticItem("trail_gold", "Chuva de Ouro Cósmico", CosmeticType.TAP_TRAIL, Rarity.LEGENDARY, "Moedas cósmicas e estrelas douradas explodem.", "🌟"),
        // Titles
        CosmeticItem("title_cadet", "Cadete Espacial", CosmeticType.PLAYER_TITLE, Rarity.COMMON, "Primeiros passos rumo às estrelas.", "🚀"),
        CosmeticItem("title_miner", "Minerador Interestelar", CosmeticType.PLAYER_TITLE, Rarity.RARE, "Especialista em riqueza de asteroides.", "⛏️"),
        CosmeticItem("title_archon", "Arconte da Galáxia", CosmeticType.PLAYER_TITLE, Rarity.EPIC, "Respeitado em todos os sistemas solares.", "👑"),
        CosmeticItem("title_creator", "Lorde da Singularidade", CosmeticType.PLAYER_TITLE, Rarity.LEGENDARY, "Mestre incontestável do cosmos infinito.", "🌌")
    )

    val DEFAULT_DAILY_QUESTS = listOf(
        DailyQuest("q_clicks", "Dedos de Titã", "Dê 200 cliques no Núcleo Cósmico", 200, 0, 15, false, "👆"),
        DailyQuest("q_duel", "Gladiador Astral", "Participe de 2 Duelos na Arena", 2, 0, 20, false, "⚔️"),
        DailyQuest("q_upgrades", "Engenharia Galáctica", "Compre 10 melhorias automáticas", 10, 0, 25, false, "🔧"),
        DailyQuest("q_comet", "Caçador de Cometas", "Colete 1 Cometa Dourado surpresa", 1, 0, 30, false, "🌠")
    )
}
