angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", {
    url: "/osa/:osaId",
    resolve: {
        osa: (ops, $stateParams) => ops.one("tekstit", $stateParams.osaId).get(),
        kommentit: (osa) => osa.all("kommentit").getList()
    },
    onEnter: (osa) =>
        Murupolku.register("root.koulutustoimija.opetussuunnitelmat.sisalto.osa", osa.tekstiKappale.nimi),
    views: {
        "": {
            controller: ($state, $stateParams, $location, $scope, $rootScope, $document, $timeout, osa, nimiLataaja) => {
                osa.lapset = undefined;
                $scope.edit = EditointikontrollitService.createRestangular($scope, "osa", osa);
                nimiLataaja(osa.tekstiKappale.muokkaaja)
                    .then(nimi => $scope.osa.tekstiKappale.$$nimi = nimi);
                $scope.remove = () => {
                    osa.remove()
                        .then(() => {
                            NotifikaatioService.onnistui("poisto-tekstikappale-onnistui");
                            EditointikontrollitService.cancel()
                                .then(() => {
                                    $timeout(() => {
                                        $state.reload("root");
                                        $state.go("root.koulutustoimija.opetussuunnitelmat.poistetut", $stateParams, { reload: true });
                                    });
                                });
                        });
                };

                const clickHandler = (event) => {
                    var ohjeEl = angular.element(event.target).closest('.popover, .popover-element');
                    if (ohjeEl.length === 0) {
                        $rootScope.$broadcast('ohje:closeAll');
                    }
                };

                const installClickHandler = () => {
                    $document.off('click', clickHandler);
                    $timeout(() => {
                        $document.on('click', clickHandler);
                    });
                };

                $scope.$on('$destroy', function () {
                    $document.off('click', clickHandler);
                });

                installClickHandler();
            }
        },
        tekstikappale: {
            controller: ($scope, osa) => {}
        },
        tutkinnonosat: {
            controller: ($scope, osa) => {}
        },
        tutkinnonosa: {
            controller: ($scope, osa) => {
                osa.tosa.arvioinnista = osa.tosa.arvioinnista || {};
                osa.tosa.tavatjaymparisto = osa.tosa.tavatjaymparisto || {};
            }
        },
        tutkinnonosaryhma: {
            controller: ($scope, osa) => {}
        },
        suorituspolut: {
            controller: ($scope, osa) => {

                $scope.misc = {
                   tosat: _([ {
                    "id" : 7441,
                    "laajuus" : 15.00,
                    "nimi" : {
                        "fi" : "Yritystoiminnan suunnittelu",
                        "sv" : "Planering av företagsverksamhet",
                        "_id" : "192071"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "632"
                    }, {
                    "id" : 7442,
                    "laajuus" : 5.00,
                    "nimi" : {
                        "fi" : "Työpaikkaohjaajaksi valmentautuminen",
                        "sv" : "Förberedelse för arbetsplatshandledaruppgifter",
                        "_id" : "398804"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "633"
                    }, {
                    "id" : 7443,
                    "laajuus" : 15.00,
                    "nimi" : {
                        "fi" : "Yrityksessä toimiminen",
                        "sv" : "Arbeta i ett företag",
                        "_id" : "191568"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "634"
                    }, {
                    "id" : 7444,
                    "laajuus" : 15.00,
                    "nimi" : {
                        "fi" : "Huippuosaajana toimiminen",
                        "sv" : "Arbete som kräver spetskompetens",
                        "_id" : "398803"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "635"
                    }, {
                    "id" : 7445,
                    "laajuus" : 11.00,
                    "muokattu" : 1438946282311,
                    "nimi" : {
                        "fi" : "Viestintä- ja vuorovaikutusosaaminen",
                        "sv" : "Kunnande i kommunikation och interaktion",
                        "_id" : "194764"
                    },
                    "tyyppi" : "tutke2",
                    "_tutkinnonOsa" : "636"
                    }, {
                    "id" : 7446,
                    "laajuus" : 9.00,
                    "nimi" : {
                        "fi" : "Matemaattis-luonnontieteellinen osaaminen",
                        "sv" : "Kunnande i matematik och naturvetenskap",
                        "_id" : "193255"
                    },
                    "tyyppi" : "tutke2",
                    "_tutkinnonOsa" : "637"
                    }, {
                    "id" : 7447,
                    "laajuus" : 8.00,
                    "nimi" : {
                        "fi" : "Yhteiskunnassa ja työelämässä tarvittava osaaminen",
                        "sv" : "Kunnande som behövs i samhället och i arbetslivet",
                        "_id" : "193685"
                    },
                    "tyyppi" : "tutke2",
                    "_tutkinnonOsa" : "638"
                    }, {
                    "id" : 7448,
                    "laajuus" : 7.00,
                    "nimi" : {
                        "fi" : "Sosiaalinen ja kulttuurinen osaaminen",
                        "sv" : "Socialt och kulturellt kunnande",
                        "_id" : "194390"
                    },
                    "tyyppi" : "tutke2",
                    "_tutkinnonOsa" : "639"
                    }, {
                    "id" : 7449,
                    "laajuus" : 45.00,
                    "muokattu" : 1435052759312,
                    "nimi" : {
                        "fi" : "Auton tai moottoripyörän huoltaminen",
                        "sv" : "Bil- eller motorcykelservice",
                        "_id" : "235044"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "7705"
                    }, {
                    "id" : 7980,
                    "laajuus" : 30.00,
                    "muokattu" : 1417690754950,
                    "nimi" : {
                        "fi" : "Huolto- ja korjaustyöt",
                        "sv" : "Service- och reparationsarbeten",
                        "_id" : "235629"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "7706"
                    }, {
                    "id" : 7981,
                    "laajuus" : 45.00,
                    "muokattu" : 1417689970832,
                    "nimi" : {
                        "fi" : "Auton korjaaminen",
                        "sv" : "Bilreparation",
                        "_id" : "227339"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "7707"
                    }, {
                    "id" : 7982,
                    "laajuus" : 30.00,
                    "muokattu" : 1419867953339,
                    "nimi" : {
                        "fi" : "Pohjustustyöt",
                        "sv" : "Grundningsarbeten",
                        "_id" : "265695"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "7708"
                    }, {
                    "id" : 7983,
                    "laajuus" : 30.00,
                    "muokattu" : 1419356414266,
                    "nimi" : {
                        "fi" : "Pintavauriotyöt",
                        "sv" : "Ytskadearbeten",
                        "_id" : "375596"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "7709"
                    }, {
                    "id" : 7984,
                    "laajuus" : 30.00,
                    "muokattu" : 1417777327943,
                    "nimi" : {
                        "fi" : "Mittaus- ja korivauriotyöt",
                        "sv" : "Mätnings- och karosskadearbeten",
                        "_id" : "276160"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "9000"
                    }, {
                    "id" : 7985,
                    "laajuus" : 30.00,
                    "muokattu" : 1420217297852,
                    "nimi" : {
                        "fi" : "Ruiskumaalaustyöt",
                        "sv" : "Sprutmålningsarbeten",
                        "_id" : "389794"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "9001"
                    }, {
                    "id" : 7986,
                    "laajuus" : 45.00,
                    "muokattu" : 1417783779548,
                    "nimi" : {
                        "fi" : "Myynti ja tuotetuntemus",
                        "sv" : "Försäljning och produktkännedom",
                        "_id" : "279547"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "9002"
                    }, {
                    "id" : 7987,
                    "laajuus" : 30.00,
                    "muokattu" : 1417775303488,
                    "nimi" : {
                        "fi" : "Markkinointi ja asiakaspalvelu",
                        "sv" : "Marknadsföring och kundservice",
                        "_id" : "275273"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "9003"
                    }, {
                    "id" : 10050,
                    "laajuus" : 30.00,
                    "muokattu" : 1420299288186,
                    "nimi" : {
                        "fi" : "Varastonhallinta",
                        "sv" : "Lagerhållning",
                        "_id" : "391009"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "9006"
                    }, {
                    "id" : 10051,
                    "laajuus" : 45.00,
                    "muokattu" : 1419334468346,
                    "nimi" : {
                        "fi" : "Pienkoneiden korjaaminen",
                        "sv" : "Reparation av småmaskiner",
                        "_id" : "375471"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "9007"
                    }, {
                    "id" : 10052,
                    "laajuus" : 15.00,
                    "muokattu" : 1420219378416,
                    "nimi" : {
                        "fi" : "Sähkövarusteiden mittaus ja korjaus",
                        "sv" : "Mätning och reparation av elutrustning",
                        "_id" : "390016"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "9008"
                    }, {
                    "id" : 10053,
                    "laajuus" : 15.00,
                    "muokattu" : 1435055517667,
                    "nimi" : {
                        "fi" : "Rengastyöt",
                        "sv" : "Däckarbeten",
                        "_id" : "389664"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "9009"
                    }, {
                    "id" : 10054,
                    "laajuus" : 15.00,
                    "muokattu" : 1417696917105,
                    "nimi" : {
                        "fi" : "Kuorma-auton alusta- ja hallintalaitteiden korjaus",
                        "sv" : "Reparation av lastbilschassier och manöverorgan",
                        "_id" : "241202"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "10640"
                    }, {
                    "id" : 10055,
                    "laajuus" : 15.00,
                    "muokattu" : 1435055907368,
                    "nimi" : {
                        "fi" : "Moottorin ja voimansiirron huolto ja korjaus",
                        "sv" : "Motor- och transmissionsservice och -reparation",
                        "_id" : "277148"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "10641"
                    }, {
                    "id" : 10056,
                    "laajuus" : 15.00,
                    "muokattu" : 1417702748720,
                    "nimi" : {
                        "fi" : "Hydrauliikka- ja pneumatiikkajärjestelmien korjaus",
                        "sv" : "Reparation av hydraulik- och pneumatiksystem",
                        "_id" : "240738"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "10642"
                    }, {
                    "id" : 10057,
                    "laajuus" : 15.00,
                    "muokattu" : 1419271326918,
                    "nimi" : {
                        "fi" : "Paineilmajarrujen testaus ja korjaus",
                        "sv" : "Testning och reparation av tryckluftsbromsar",
                        "_id" : "373184"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "10643"
                    }, {
                    "id" : 10058,
                    "laajuus" : 15.00,
                    "muokattu" : 1417703242897,
                    "nimi" : {
                        "fi" : "Maalauksen esikäsittelytyöt",
                        "sv" : "Förberedande arbeten inför billackering",
                        "_id" : "242195"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "10644"
                    }, {
                    "id" : 10059,
                    "laajuus" : 15.00,
                    "muokattu" : 1417704292570,
                    "nimi" : {
                        "fi" : "Auton turvavarustetyöt",
                        "sv" : "Arbeten med bilens säkerhetsutrustning",
                        "_id" : "235301"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "10645"
                    }, {
                    "id" : 11690,
                    "laajuus" : 15.00,
                    "muokattu" : 1417704408430,
                    "nimi" : {
                        "fi" : "Auton korin sähkövarustetyöt",
                        "sv" : "Arbeten med karossens elutrustning",
                        "_id" : "225838"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "10646"
                    }, {
                    "id" : 11691,
                    "laajuus" : 15.00,
                    "muokattu" : 1417704724332,
                    "nimi" : {
                        "fi" : "Auton lisävarustetyöt",
                        "sv" : "Arbeten med bilens tilläggsutrustning",
                        "_id" : "234802"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "10647"
                    }, {
                    "id" : 11692,
                    "laajuus" : 15.00,
                    "muokattu" : 1417704957524,
                    "nimi" : {
                        "fi" : "Eri materiaalien korjaus- ja maalaustyöt",
                        "sv" : "Reparation och målning av olika material",
                        "_id" : "235475"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "10648"
                    }, {
                    "id" : 11693,
                    "laajuus" : 15.00,
                    "muokattu" : 1417705183185,
                    "nimi" : {
                        "fi" : "Autokorintyöt",
                        "sv" : "Karossarbeten",
                        "_id" : "225246"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "10649"
                    }, {
                    "id" : 11694,
                    "laajuus" : 15.00,
                    "muokattu" : 1417706492525,
                    "nimi" : {
                        "fi" : "Kuviomaalaustyöt",
                        "sv" : "Figurmålningsarbeten",
                        "_id" : "241412"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "12380"
                    }, {
                    "id" : 11695,
                    "laajuus" : 15.00,
                    "muokattu" : 1417706679760,
                    "nimi" : {
                        "fi" : "Ajoneuvomyyntityö",
                        "sv" : "Fordonsförsäljning",
                        "_id" : "224680"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "12381"
                    }, {
                    "id" : 11696,
                    "laajuus" : 15.00,
                    "muokattu" : 1417707411787,
                    "nimi" : {
                        "fi" : "Käytettyjen autojen myyntityö",
                        "sv" : "Försäljning av begagnade bilar",
                        "_id" : "241585"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "12382"
                    }, {
                    "id" : 11697,
                    "laajuus" : 15.00,
                    "muokattu" : 1417707859988,
                    "nimi" : {
                        "fi" : "Hyöty- ja erityisajoneuvojen myyntityö",
                        "sv" : "Försäljning av nytto- och specialfordon",
                        "_id" : "240987"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "12383"
                    }, {
                    "id" : 11698,
                    "laajuus" : 15.00,
                    "muokattu" : 1420300853349,
                    "nimi" : {
                        "fi" : "Vapaa-ajan ajoneuvojen sekä niiden varaosien myyntityö",
                        "sv" : "Försäljning av fritidsfordon samt deras reservdelar",
                        "_id" : "390471"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "12384"
                    }, {
                    "id" : 11699,
                    "laajuus" : 15.00,
                    "muokattu" : 1417709016413,
                    "nimi" : {
                        "fi" : "Lisävaruste- ja tarvikemyyntityö",
                        "sv" : "Försäljning av tilläggsutrustning och tillbehör",
                        "_id" : "242024"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "12385"
                    }, {
                    "id" : 13290,
                    "laajuus" : 15.00,
                    "muokattu" : 1419868713308,
                    "nimi" : {
                        "fi" : "Rengas- ja vannemyynti",
                        "sv" : "Försäljning av däck och fälgar",
                        "_id" : "382179"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "12386"
                    }, {
                    "id" : 13291,
                    "laajuus" : 30.00,
                    "muokattu" : 1435056161608,
                    "nimi" : {
                        "fi" : "Maastoajoneuvojen, mopojen ja mopoautojen huolto ja korjaus",
                        "sv" : "Service och reparation av terrängfordon, mopeder och mopedbilar",
                        "_id" : "269118"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "12387"
                    }, {
                    "id" : 13292,
                    "laajuus" : 30.00,
                    "muokattu" : 1417781391796,
                    "nimi" : {
                        "fi" : "Moottoripyörien huolto ja korjaus",
                        "sv" : "Service och reparation av motorcyklar",
                        "_id" : "278597"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "12388"
                    }, {
                    "id" : 13293,
                    "laajuus" : 30.00,
                    "muokattu" : 1427376799613,
                    "nimi" : {
                        "fi" : "Venemoottoreiden ja varusteiden asennus, huolto ja korjaus",
                        "sv" : "Montering, service och reparation av båtmotorer och -utrustning",
                        "_id" : "391157"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "12389"
                    }, {
                    "id" : 13294,
                    "laajuus" : 15.00,
                    "muokattu" : 1420298460603,
                    "nimi" : {
                        "fi" : "Varaosavaraston hoitaminen",
                        "sv" : "Att ansvara för ett reservdelslager",
                        "_id" : "390882"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "13820"
                    }, {
                    "id" : 13295,
                    "laajuus" : 15.00,
                    "muokattu" : 1420296566795,
                    "nimi" : {
                        "fi" : "Varaosatyö ja varaston hallinta",
                        "sv" : "Reservdelsarbete och lagerhållning",
                        "_id" : "390739"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "13821"
                    }, {
                    "id" : 13297,
                    "laajuus" : 15.00,
                    "muokattu" : 1420220127218,
                    "nimi" : {
                        "fi" : "Uusien autojen myyntityö",
                        "sv" : "Försäljning av nya bilar",
                        "_id" : "390173"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "13823"
                    }, {
                    "id" : 13299,
                    "laajuus" : 15.00,
                    "muokattu" : 1420294575876,
                    "nimi" : {
                        "fi" : "Uusien tuotteiden varustelu ja kokoaminen",
                        "sv" : "Utrustande och hopmontering av nya produkter",
                        "_id" : "390326"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "13825"
                    }, {
                    "id" : 14634,
                    "laajuus" : 15.00,
                    "muokattu" : 1417784616288,
                    "nimi" : {
                        "fi" : "Osto- ja hankintatoiminta",
                        "sv" : "Inköps- och anskaffningsverksamhet",
                        "_id" : "280018"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "15110"
                    }, {
                    "id" : 14635,
                    "laajuus" : 30.00,
                    "muokattu" : 1420295941084,
                    "nimi" : {
                        "fi" : "Varaosamyyntityö ja asiakaspalvelu",
                        "sv" : "Reservdelsförsäljning och kundservice",
                        "_id" : "390618"
                    },
                    "tyyppi" : "normaali",
                    "_tutkinnonOsa" : "15111"
                    } ])
                    .indexBy("id")
                    .value()
                };

                $scope.perusteRakenne = {
                    "id" : 7669,
                    "kuvaus" : {
                        "fi" : "<p>Ammatillisena peruskoulutuksena suoritettava perustutkinto muodostuu ammatillisista tutkinnon osista(135 osaamispistettä), yhteisistä tutkinnon osista (35 osaamispistettä) ja vapaasti valittavista tutkinnonosista (10 osaamispistettä).</p>\n\n<p>Ammatillisen perustutkinnon laajuus on 180 osaamispistettä. Ammatillisessa peruskoulutuksessa opiskelija voi yksilöllisesti sisällyttää perustutkintoonsa enemmäntutkinnon osia, jos se on tarpeellista työelämän alakohtaisten tai paikallisten ammattitaitovaatimusten taiopiskelijan ammattitaidon syventämisen kannalta.</p>\n\n<p>Autoalan perustutkinto sisältää kuusi osaamisalaa, autotekniikan osaamisalan (ajoneuvoasentaja),autokorinkorjauksen osaamisalan (autokorinkorjaaja), automaalauksen osaamisalan (automaalari),automyynnin osaamisalan (automyyjä), varaosamyynnin osaamisala (varaosamyyjä) ja moottorikäyttöistenpienkoneiden korjauksen osaamisalan (pienkonekorjaaja)</p>",
                        "sv" : "<p>En grundexamen som avläggs som en grundläggande yrkesutbildning är uppbyggd av yrkesinriktade examensdelar (135 kompetenspoäng), av gemensamma examensdelar (35 kompetenspoäng) och av fritt valbaraexamensdelar (10 kompetenspoäng). Omfattningen av en yrkesinriktad grundexamen är 180 kompetenspoäng.</p>\n\n<p>Den studerande kan inom grundläggande yrkesutbildning individuellt ta in fler examensdelar om det är nödvändigt med tanke på arbetslivets branschvisa eller lokala krav på yrkesskicklighet eller en fördjupning av studerandes yrkeskompetens.</p>\n\n<p>Grundexamen inom bilbranschen består av sex kompetensområden, kompetensområdet för bilplåtslagning (bilplåtslagare), kompetensområdet för billackering (billackerare), kompetensområdet för bilförsäljning (bilförsäljare), kompetensområdet för bilteknik (fordonsmekaniker), kompetensområdet för reservdelsförsäljning (reservdelsförsäljare), kompetensområdet för reparation av motordrivna småmaskiner (småmaskinsreparatör).</p>",
                        "_id" : "1024462"
                    },
                    "vieras" : null,
                    "nimi" : {
                        "fi" : "Autoalan perustutkinto",
                        "_id" : "618917"
                    },
                    "rooli" : "määritelty",
                    "muodostumisSaanto" : {
                        "laajuus" : {
                            "minimi" : 180,
                            "maksimi" : 180,
                            "yksikko" : null
                        },
                        "koko" : null
                    },
                    "osaamisala" : null,
                    "osat" : [ {
                        "id" : 1273573,
                        "kuvaus" : null,
                        "vieras" : null,
                        "nimi" : {
                            "fi" : "Ammatilliset tutkinnon osat",
                            "sv" : "Yrkesinriktade examensdelar",
                            "_id" : "734995"
                        },
                        "rooli" : "määritelty",
                        "muodostumisSaanto" : {
                            "laajuus" : {
                                "minimi" : 135,
                                "maksimi" : 135,
                                "yksikko" : null
                            },
                            "koko" : null
                        },
                        "osaamisala" : null,
                        "osat" : [ {
                            "id" : 1273574,
                            "kuvaus" : null,
                            "vieras" : null,
                            "nimi" : {
                                "fi" : "Autokorinkorjauksen osaamisala",
                                "sv" : "Kompetensområdet för bilplåtslagning",
                                "_id" : "614934"
                            },
                            "rooli" : "osaamisala",
                            "muodostumisSaanto" : {
                                "laajuus" : {
                                    "minimi" : 135,
                                    "maksimi" : 135,
                                    "yksikko" : null
                                },
                                "koko" : null
                            },
                            "osaamisala" : {
                                "nimi" : {
                                    "fi" : "Autokorinkorjauksen osaamisala",
                                    "sv" : "Kompetensområde för bilplåtslagare",
                                    "_id" : "185501"
                                },
                                "osaamisalakoodiArvo" : "1525",
                                "osaamisalakoodiUri" : "osaamisala_1525"
                            },
                            "osat" : [ {
                                "id" : 1273575,
                                "kuvaus" : null,
                                "vieras" : null,
                                "nimi" : {
                                    "fi" : "Pakolliset tutkinnon osat",
                                    "sv" : "Obligatoriska examensdelar",
                                    "_id" : "941404"
                                },
                                "rooli" : "määritelty",
                                "muodostumisSaanto" : {
                                    "laajuus" : {
                                        "minimi" : 90,
                                        "maksimi" : 90,
                                        "yksikko" : null
                                    },
                                    "koko" : null
                                },
                                "osaamisala" : null,
                                "osat" : [ {
                                    "id" : 1273576,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : true,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7980"
                                }, {
                                    "id" : 1273577,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : true,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7983"
                                }, {
                                    "id" : 1273578,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : true,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7984"
                                } ],
                                "versioId" : null
                            }, {
                                "id" : 1273579,
                                "kuvaus" : null,
                                "vieras" : null,
                                "nimi" : {
                                    "fi" : "Valinnaiset tutkinnon osat",
                                    "sv" : "Valbara examensdelar",
                                    "_id" : "734997"
                                },
                                "rooli" : "määritelty",
                                "muodostumisSaanto" : {
                                    "laajuus" : {
                                        "minimi" : 45,
                                        "maksimi" : 45,
                                        "yksikko" : null
                                    },
                                    "koko" : null
                                },
                                "osaamisala" : null,
                                "osat" : [ {
                                    "id" : 1273620,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "10058"
                                }, {
                                    "id" : 1273621,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "10059"
                                }, {
                                    "id" : 1273622,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11690"
                                }, {
                                    "id" : 1273623,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11691"
                                }, {
                                    "id" : 1273624,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "13295"
                                }, {
                                    "id" : 1273625,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7441"
                                }, {
                                    "id" : 1273626,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7442"
                                }, {
                                    "id" : 1273627,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7443"
                                }, {
                                    "id" : 1273628,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7444"
                                }, {
                                    "id" : 1273629,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "nimi" : {
                                        "fi" : "Tutkinnon osa ammatillisesta perustutkinnosta",
                                        "sv" : "Examensdel från en yrkesinriktad grundexamen",
                                        "_id" : "734998"
                                    },
                                    "rooli" : "määrittelemätön",
                                    "muodostumisSaanto" : {
                                        "laajuus" : {
                                            "minimi" : 10,
                                            "maksimi" : 15,
                                            "yksikko" : null
                                        },
                                        "koko" : null
                                    },
                                    "osaamisala" : null,
                                    "osat" : [ ],
                                    "versioId" : null
                                }, {
                                    "id" : 1273630,
                                    "kuvaus" : {
                                        "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp",
                                        "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
                                        "_id" : "734999"
                                    },
                                    "vieras" : null,
                                    "nimi" : {
                                        "fi" : "Tutkinnon osa ammattitutkinnosta tai erikoisammattitutkinnosta",
                                        "sv" : "Examensdel från en yrkesexamen eller en specialyrkesexamen",
                                        "_id" : "735020"
                                    },
                                    "rooli" : "määrittelemätön",
                                    "muodostumisSaanto" : null,
                                    "osaamisala" : null,
                                    "osat" : [ ],
                                    "versioId" : null
                                }, {
                                    "id" : 1273631,
                                    "kuvaus" : {
                                        "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp.",
                                        "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
                                        "_id" : "735021"
                                    },
                                    "vieras" : null,
                                    "nimi" : {
                                        "fi" : "Tutkinnon osa ammattikorkeakouluopinnoista",
                                        "sv" : "Examensdel från yrkeshögskolestudier",
                                        "_id" : "735022"
                                    },
                                    "rooli" : "määrittelemätön",
                                    "muodostumisSaanto" : null,
                                    "osaamisala" : null,
                                    "osat" : [ ],
                                    "versioId" : null
                                }, {
                                    "id" : 1273632,
                                    "kuvaus" : {
                                        "fi" : "Tämä voi sisältää työelämän alueellisten ja paikallisten ammattitaitovaatimusten ja opiskelijan ammattitaidon syventämistarpeiden mukaisia tutkinnon osia, joiden tulee vastata laajemmin paikallisiin ammattitaitovaatimuksiin kuin pelkästään yhden yrityksen tarpeisiin. Nämä tutkinnon osat nimetään työelämän toimintakokonaisuuden pohjalta ja niille määritellään laajuus osaamispisteinä (5-15 osp). Lisäksi määritellään tutkinnon osien ammattitaitovaatimukset, osaamisen arviointi ja ammattitaidon osoittamistavat Opetushallituksen määräyksen (Osaamisen tunnistamisen ja tunnustamisen mitoituksen periaatteet ja arvosanojen muuntaminen ammatillisessa peruskoulutuksessa 93/011/2014) liitteessä olevan arviointitaulukon mukaisesti.",
                                        "sv" : "Detta kan vara examensdelar som tillgodoser de regionala och lokala kraven på yrkesskicklighet och studerandes behov av att fördjupa sin yrkesskicklighet. Examensdelarna bör svara mot mer omfattande lokala krav på yrkesskicklighet än endast mot behoven hos ett enskilt företag. Dessa examensdelar nämns utifrån en verksamhetshelhet inom arbetslivet och omfattningen på dem bestäms utifrån kompetenspoäng (5-15 kp). Dessutom bestäms för examensdelarna kraven på yrkesskicklighet, bedömning av kunnandet och sätten att påvisa yrkesskickligheten enligt bedömningstabellen i bilagan i Utbildningsstyrelsens föreskrift. (Principer för dimensioneringen av identifiering och erkännande av kunnande samt omvandling av vitsord i den grundläggande yrkesutbildningen 93/011/2014).",
                                        "_id" : "735023"
                                    },
                                    "vieras" : null,
                                    "nimi" : {
                                        "fi" : "Paikallisiin ammattitaitovaatimuksiin perustuvia tutkinnon osia",
                                        "sv" : "Examensdelar som grundar sig på lokala krav på yrkesskicklighet",
                                        "_id" : "735024"
                                    },
                                    "rooli" : "määrittelemätön",
                                    "muodostumisSaanto" : {
                                        "laajuus" : {
                                            "minimi" : 5,
                                            "maksimi" : 15,
                                            "yksikko" : null
                                        },
                                        "koko" : null
                                    },
                                    "osaamisala" : null,
                                    "osat" : [ ],
                                    "versioId" : null
                                }, {
                                    "id" : 1273633,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "nimi" : {
                                        "fi" : "Tutkinnon osa vapaasti valittavista tutkinnon osista",
                                        "sv" : "Examensdel från de fritt valbara examensdelarna",
                                        "_id" : "735025"
                                    },
                                    "rooli" : "määrittelemätön",
                                    "muodostumisSaanto" : {
                                        "laajuus" : {
                                            "minimi" : 5,
                                            "maksimi" : 15,
                                            "yksikko" : null
                                        },
                                        "koko" : null
                                    },
                                    "osaamisala" : null,
                                    "osat" : [ ],
                                    "versioId" : null
                                } ],
                                "versioId" : null
                            } ],
                            "versioId" : null
                        }, {
                            "id" : 1273634,
                            "kuvaus" : null,
                            "vieras" : null,
                            "nimi" : {
                                "fi" : "Automaalauksen osaamisala",
                                "sv" : "Kompetensområdet för billackering",
                                "_id" : "735026"
                            },
                            "rooli" : "osaamisala",
                            "muodostumisSaanto" : {
                                "laajuus" : {
                                    "minimi" : 135,
                                    "maksimi" : 135,
                                    "yksikko" : null
                                },
                                "koko" : null
                            },
                            "osaamisala" : {
                                "nimi" : {
                                    "fi" : "Automaalauksen osaamisala",
                                    "sv" : "Kompetensområde för billackering",
                                    "_id" : "185502"
                                },
                                "osaamisalakoodiArvo" : "1526",
                                "osaamisalakoodiUri" : "osaamisala_1526"
                            },
                            "osat" : [ {
                                "id" : 1273635,
                                "kuvaus" : null,
                                "vieras" : null,
                                "nimi" : {
                                    "fi" : "Pakolliset tutkinnon osat",
                                    "sv" : "Obligatoriska examensdelar",
                                    "_id" : "941405"
                                },
                                "rooli" : "määritelty",
                                "muodostumisSaanto" : {
                                    "laajuus" : {
                                        "minimi" : 90,
                                        "maksimi" : 90,
                                        "yksikko" : null
                                    },
                                    "koko" : null
                                },
                                "osaamisala" : null,
                                "osat" : [ {
                                    "id" : 1273636,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : true,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7980"
                                }, {
                                    "id" : 1273637,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : true,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7982"
                                }, {
                                    "id" : 1273638,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : true,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7985"
                                } ],
                                "versioId" : null
                            }, {
                                "id" : 1273639,
                                "kuvaus" : null,
                                "vieras" : null,
                                "nimi" : {
                                    "fi" : "Valinnaiset tutkinnon osat",
                                    "sv" : "Valbara examensdelar",
                                    "_id" : "735027"
                                },
                                "rooli" : "määritelty",
                                "muodostumisSaanto" : {
                                    "laajuus" : {
                                        "minimi" : 45,
                                        "maksimi" : 45,
                                        "yksikko" : null
                                    },
                                    "koko" : null
                                },
                                "osaamisala" : null,
                                "osat" : [ {
                                    "id" : 1273640,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "10059"
                                }, {
                                    "id" : 1273641,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11690"
                                }, {
                                    "id" : 1273642,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11691"
                                }, {
                                    "id" : 1273643,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11692"
                                }, {
                                    "id" : 1273644,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11693"
                                }, {
                                    "id" : 1273645,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11694"
                                }, {
                                    "id" : 1273646,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "13295"
                                }, {
                                    "id" : 1273647,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7441"
                                }, {
                                    "id" : 1273648,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7442"
                                }, {
                                    "id" : 1273649,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7443"
                                }, {
                                    "id" : 1273650,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7444"
                                }, {
                                    "id" : 1273651,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "nimi" : {
                                        "fi" : "Tutkinnon osa ammatillisesta perustutkinnosta",
                                        "sv" : "Examensdel från en yrkesinriktad grundexamen",
                                        "_id" : "735028"
                                    },
                                    "rooli" : "määrittelemätön",
                                    "muodostumisSaanto" : {
                                        "laajuus" : {
                                            "minimi" : 10,
                                            "maksimi" : 15,
                                            "yksikko" : null
                                        },
                                        "koko" : null
                                    },
                                    "osaamisala" : null,
                                    "osat" : [ ],
                                    "versioId" : null
                                }, {
                                    "id" : 1273652,
                                    "kuvaus" : {
                                        "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp",
                                        "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
                                        "_id" : "735029"
                                    },
                                    "vieras" : null,
                                    "nimi" : {
                                        "fi" : "Tutkinnon osa ammattitutkinnosta tai erikoisammattitutkinnosta",
                                        "sv" : "Examensdel från en yrkesexamen eller en specialyrkesexamen",
                                        "_id" : "735050"
                                    },
                                    "rooli" : "määrittelemätön",
                                    "muodostumisSaanto" : null,
                                    "osaamisala" : null,
                                    "osat" : [ ],
                                    "versioId" : null
                                }, {
                                    "id" : 1273653,
                                    "kuvaus" : {
                                        "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp.",
                                        "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
                                        "_id" : "735051"
                                    },
                                    "vieras" : null,
                                    "nimi" : {
                                        "fi" : "Tutkinnon osa ammattikorkeakouluopinnoista",
                                        "sv" : "Examensdel från yrkeshögskolestudier",
                                        "_id" : "735052"
                                    },
                                    "rooli" : "määrittelemätön",
                                    "muodostumisSaanto" : null,
                                    "osaamisala" : null,
                                    "osat" : [ ],
                                    "versioId" : null
                                }, {
                                    "id" : 1273654,
                                    "kuvaus" : {
                                        "fi" : "Tämä voi sisältää työelämän alueellisten ja paikallisten ammattitaitovaatimusten ja opiskelijan ammattitaidon syventämistarpeiden mukaisia tutkinnon osia, joiden tulee vastata laajemmin paikallisiin ammattitaitovaatimuksiin kuin pelkästään yhden yrityksen tarpeisiin. Nämä tutkinnon osat nimetään työelämän toimintakokonaisuuden pohjalta ja niille määritellään laajuus osaamispisteinä (5-15 osp). Lisäksi määritellään tutkinnon osien ammattitaitovaatimukset, osaamisen arviointi ja ammattitaidon osoittamistavat Opetushallituksen määräyksen (Osaamisen tunnistamisen ja tunnustamisen mitoituksen periaatteet ja arvosanojen muuntaminen ammatillisessa peruskoulutuksessa 93/011/2014) liitteessä olevan arviointitaulukon mukaisesti.",
                                        "sv" : "Detta kan vara examensdelar som tillgodoser de regionala och lokala kraven på yrkesskicklighet och studerandes behov av att fördjupa sin yrkesskicklighet. Examensdelarna bör svara mot mer omfattande lokala krav på yrkesskicklighet än endast mot behoven hos ett enskilt företag. Dessa examensdelar nämns utifrån en verksamhetshelhet inom arbetslivet och omfattningen på dem bestäms utifrån kompetenspoäng (5-15 kp). Dessutom bestäms för examensdelarna kraven på yrkesskicklighet, bedömning av kunnandet och sätten att påvisa yrkesskickligheten enligt bedömningstabellen i bilagan i Utbildningsstyrelsens föreskrift. (Principer för dimensioneringen av identifiering och erkännande av kunnande samt omvandling av vitsord i den grundläggande yrkesutbildningen 93/011/2014).",
                                        "_id" : "735053"
                                    },
                                    "vieras" : null,
                                    "nimi" : {
                                        "fi" : "Paikallisiin ammattitaitovaatimuksiin perustuvia tutkinnon osia",
                                        "sv" : "Examensdelar som grundar sig på lokala krav på yrkesskicklighet",
                                        "_id" : "735054"
                                    },
                                    "rooli" : "määrittelemätön",
                                    "muodostumisSaanto" : {
                                        "laajuus" : {
                                            "minimi" : 5,
                                            "maksimi" : 15,
                                            "yksikko" : null
                                        },
                                        "koko" : null
                                    },
                                    "osaamisala" : null,
                                    "osat" : [ ],
                                    "versioId" : null
                                }, {
                                    "id" : 1273655,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "nimi" : {
                                        "fi" : "Tutkinnon osa vapaasti valittavista tutkinnon osista",
                                        "sv" : "Examensdel från de fritt valbara examensdelarna",
                                        "_id" : "735055"
                                    },
                                    "rooli" : "määrittelemätön",
                                    "muodostumisSaanto" : {
                                        "laajuus" : {
                                            "minimi" : 5,
                                            "maksimi" : 15,
                                            "yksikko" : null
                                        },
                                        "koko" : null
                                    },
                                    "osaamisala" : null,
                                    "osat" : [ ],
                                    "versioId" : null
                                } ],
                                "versioId" : null
                            } ],
                            "versioId" : null
                        }, {
                            "id" : 1273656,
                            "kuvaus" : null,
                            "vieras" : null,
                            "nimi" : {
                                "fi" : "Automyynnin osaamisala",
                                "sv" : "Kompetensområdet för bilförsäljning",
                                "_id" : "735056"
                            },
                            "rooli" : "osaamisala",
                            "muodostumisSaanto" : {
                                "laajuus" : {
                                    "minimi" : 135,
                                    "maksimi" : 135,
                                    "yksikko" : null
                                },
                                "koko" : null
                            },
                            "osaamisala" : {
                                "nimi" : {
                                    "fi" : "Automyynnin osaamisala",
                                    "sv" : "Kompetensområde för bilförsäljning",
                                    "_id" : "185503"
                                },
                                "osaamisalakoodiArvo" : "1527",
                                "osaamisalakoodiUri" : "osaamisala_1527"
                            },
                            "osat" : [ {
                                "id" : 1273657,
                                "kuvaus" : null,
                                "vieras" : null,
                                "nimi" : {
                                    "fi" : "Pakolliset tutkinnon osat",
                                    "sv" : "Obligatoriska examensdelar",
                                    "_id" : "941406"
                                },
                                "rooli" : "määritelty",
                                "muodostumisSaanto" : {
                                    "laajuus" : {
                                        "minimi" : 90,
                                        "maksimi" : 90,
                                        "yksikko" : null
                                    },
                                    "koko" : null
                                },
                                "osaamisala" : null,
                                "osat" : [ {
                                    "id" : 1273658,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : true,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7986"
                                }, {
                                    "id" : 1273659,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : true,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "7987"
                                }, {
                                    "id" : 1273660,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : true,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "14634"
                                } ],
                                "versioId" : null
                            }, {
                                "id" : 1273661,
                                "kuvaus" : null,
                                "vieras" : null,
                                "nimi" : {
                                    "fi" : "Valinnaiset tutkinnon osat",
                                    "sv" : "Valbara examensdelar",
                                    "_id" : "735057"
                                },
                                "rooli" : "määritelty",
                                "muodostumisSaanto" : {
                                    "laajuus" : {
                                        "minimi" : 45,
                                        "maksimi" : 45,
                                        "yksikko" : null
                                    },
                                    "koko" : null
                                },
                                "osaamisala" : null,
                                "osat" : [ {
                                    "id" : 1273662,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11695"
                                }, {
                                    "id" : 1273663,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "13297"
                                }, {
                                    "id" : 1273664,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11696"
                                }, {
                                    "id" : 1273665,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11697"
                                }, {
                                    "id" : 1273666,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11698"
                                }, {
                                    "id" : 1273667,
                                    "kuvaus" : null,
                                    "vieras" : null,
                                    "pakollinen" : false,
                                    "erikoisuus" : null,
                                    "_tutkinnonOsaViite" : "11699"
        }, {
          "id" : 1273668,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13290"
        }, {
          "id" : 1273669,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13295"
        }, {
          "id" : 1273680,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7441"
        }, {
          "id" : 1273681,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7442"
        }, {
          "id" : 1273682,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7443"
        }, {
          "id" : 1273683,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7444"
        }, {
          "id" : 1273684,
          "kuvaus" : null,
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammatillisesta perustutkinnosta",
            "sv" : "Examensdel från en yrkesinriktad grundexamen",
            "_id" : "735058"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 10,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273685,
          "kuvaus" : {
            "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp",
            "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
            "_id" : "735059"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammattitutkinnosta tai erikoisammattitutkinnosta",
            "sv" : "Examensdel från en yrkesexamen eller en specialyrkesexamen",
            "_id" : "735080"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : null,
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273686,
          "kuvaus" : {
            "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp.",
            "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
            "_id" : "735081"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammattikorkeakouluopinnoista",
            "sv" : "Examensdel från yrkeshögskolestudier",
            "_id" : "735082"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : null,
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273687,
          "kuvaus" : {
            "fi" : "Tämä voi sisältää työelämän alueellisten ja paikallisten ammattitaitovaatimusten ja opiskelijan ammattitaidon syventämistarpeiden mukaisia tutkinnon osia, joiden tulee vastata laajemmin paikallisiin ammattitaitovaatimuksiin kuin pelkästään yhden yrityksen tarpeisiin. Nämä tutkinnon osat nimetään työelämän toimintakokonaisuuden pohjalta ja niille määritellään laajuus osaamispisteinä (5-15 osp). Lisäksi määritellään tutkinnon osien ammattitaitovaatimukset, osaamisen arviointi ja ammattitaidon osoittamistavat Opetushallituksen määräyksen (Osaamisen tunnistamisen ja tunnustamisen mitoituksen periaatteet ja arvosanojen muuntaminen ammatillisessa peruskoulutuksessa 93/011/2014) liitteessä olevan arviointitaulukon mukaisesti.",
            "sv" : "Detta kan vara examensdelar som tillgodoser de regionala och lokala kraven på yrkesskicklighet och studerandes behov av att fördjupa sin yrkesskicklighet. Examensdelarna bör svara mot mer omfattande lokala krav på yrkesskicklighet än endast mot behoven hos ett enskilt företag. Dessa examensdelar nämns utifrån en verksamhetshelhet inom arbetslivet och omfattningen på dem bestäms utifrån kompetenspoäng (5-15 kp). Dessutom bestäms för examensdelarna kraven på yrkesskicklighet, bedömning av kunnandet och sätten att påvisa yrkesskickligheten enligt bedömningstabellen i bilagan i Utbildningsstyrelsens föreskrift. (Principer för dimensioneringen av identifiering och erkännande av kunnande samt omvandling av vitsord i den grundläggande yrkesutbildningen 93/011/2014).",
            "_id" : "735083"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Paikallisiin ammattitaitovaatimuksiin perustuvia tutkinnon osia",
            "sv" : "Examensdelar som grundar sig på lokala krav på yrkesskicklighet",
            "_id" : "735084"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 5,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273688,
          "kuvaus" : null,
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa vapaasti valittavista tutkinnon osista",
            "sv" : "Examensdel från de fritt valbara examensdelarna",
            "_id" : "735085"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 5,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        } ],
        "versioId" : null
      } ],
      "versioId" : null
    }, {
      "id" : 1273689,
      "kuvaus" : null,
      "vieras" : null,
      "nimi" : {
        "fi" : "Autotekniikan osaamisala",
        "sv" : "Kompetensområdet för bilteknik",
        "_id" : "735086"
      },
      "rooli" : "osaamisala",
      "muodostumisSaanto" : {
        "laajuus" : {
          "minimi" : 135,
          "maksimi" : 135,
          "yksikko" : null
        },
        "koko" : null
      },
      "osaamisala" : {
        "nimi" : {
          "fi" : "Autotekniikan osaamisala",
          "sv" : "Kompetensområde för bilteknik",
          "_id" : "185504"
        },
        "osaamisalakoodiArvo" : "1528",
        "osaamisalakoodiUri" : "osaamisala_1528"
      },
      "osat" : [ {
        "id" : 1273690,
        "kuvaus" : null,
        "vieras" : null,
        "nimi" : {
          "fi" : "Pakolliset tutkinnon osat",
          "sv" : "Obligatoriska examensdelar",
          "_id" : "941407"
        },
        "rooli" : "määritelty",
        "muodostumisSaanto" : {
          "laajuus" : {
            "minimi" : 90,
            "maksimi" : 90,
            "yksikko" : null
          },
          "koko" : null
        },
        "osaamisala" : null,
        "osat" : [ {
          "id" : 1273691,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : true,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7449"
        }, {
          "id" : 1273692,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : true,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7981"
        } ],
        "versioId" : null
      }, {
        "id" : 1273693,
        "kuvaus" : null,
        "vieras" : null,
        "nimi" : {
          "fi" : "Valinnaiset tutkinnon osat",
          "sv" : "Valbara examensdelar",
          "_id" : "735087"
        },
        "rooli" : "määritelty",
        "muodostumisSaanto" : {
          "laajuus" : {
            "minimi" : 45,
            "maksimi" : 45,
            "yksikko" : null
          },
          "koko" : null
        },
        "osaamisala" : null,
        "osat" : [ {
          "id" : 1273694,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10052"
        }, {
          "id" : 1273695,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10053"
        }, {
          "id" : 1273696,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10054"
        }, {
          "id" : 1273697,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10055"
        }, {
          "id" : 1273698,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10056"
        }, {
          "id" : 1273699,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10057"
        }, {
          "id" : 1273700,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10058"
        }, {
          "id" : 1273701,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10059"
        }, {
          "id" : 1273702,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "11690"
        }, {
          "id" : 1273703,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "11691"
        }, {
          "id" : 1273704,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13295"
        }, {
          "id" : 1273705,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7441"
        }, {
          "id" : 1273706,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7442"
        }, {
          "id" : 1273707,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7443"
        }, {
          "id" : 1273708,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7444"
        }, {
          "id" : 1273709,
          "kuvaus" : null,
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammatillisesta perustutkinnosta",
            "sv" : "Examensdel från en yrkesinriktad grundexamen",
            "_id" : "735088"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 10,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273710,
          "kuvaus" : {
            "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp",
            "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
            "_id" : "735089"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammattitutkinnosta tai erikoisammattitutkinnosta",
            "sv" : "Examensdel från en yrkesexamen eller en specialyrkesexamen",
            "_id" : "735120"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : null,
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273711,
          "kuvaus" : {
            "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp.",
            "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
            "_id" : "735121"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammattikorkeakouluopinnoista",
            "sv" : "Examensdel från yrkeshögskolestudier",
            "_id" : "735122"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : null,
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273712,
          "kuvaus" : {
            "fi" : "Tämä voi sisältää työelämän alueellisten ja paikallisten ammattitaitovaatimusten ja opiskelijan ammattitaidon syventämistarpeiden mukaisia tutkinnon osia, joiden tulee vastata laajemmin paikallisiin ammattitaitovaatimuksiin kuin pelkästään yhden yrityksen tarpeisiin. Nämä tutkinnon osat nimetään työelämän toimintakokonaisuuden pohjalta ja niille määritellään laajuus osaamispisteinä (5-15 osp). Lisäksi määritellään tutkinnon osien ammattitaitovaatimukset, osaamisen arviointi ja ammattitaidon osoittamistavat Opetushallituksen määräyksen (Osaamisen tunnistamisen ja tunnustamisen mitoituksen periaatteet ja arvosanojen muuntaminen ammatillisessa peruskoulutuksessa 93/011/2014) liitteessä olevan arviointitaulukon mukaisesti.",
            "sv" : "Detta kan vara examensdelar som tillgodoser de regionala och lokala kraven på yrkesskicklighet och studerandes behov av att fördjupa sin yrkesskicklighet. Examensdelarna bör svara mot mer omfattande lokala krav på yrkesskicklighet än endast mot behoven hos ett enskilt företag. Dessa examensdelar nämns utifrån en verksamhetshelhet inom arbetslivet och omfattningen på dem bestäms utifrån kompetenspoäng (5-15 kp). Dessutom bestäms för examensdelarna kraven på yrkesskicklighet, bedömning av kunnandet och sätten att påvisa yrkesskickligheten enligt bedömningstabellen i bilagan i Utbildningsstyrelsens föreskrift. (Principer för dimensioneringen av identifiering och erkännande av kunnande samt omvandling av vitsord i den grundläggande yrkesutbildningen 93/011/2014).",
            "_id" : "735123"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Paikallisiin ammattitaitovaatimuksiin perustuvia tutkinnon osia",
            "sv" : "Examensdelar som grundar sig på lokala krav på yrkesskicklighet",
            "_id" : "735124"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 5,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273713,
          "kuvaus" : null,
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa vapaasti valittavista tutkinnon osista",
            "sv" : "Examensdel från de fritt valbara examensdelarna",
            "_id" : "735125"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 5,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        } ],
        "versioId" : null
      } ],
      "versioId" : null
    }, {
      "id" : 1273714,
      "kuvaus" : null,
      "vieras" : null,
      "nimi" : {
        "fi" : "Varaosamyynnin osaamisala",
        "sv" : "Kompetensområdet för reservdelsförsäljning",
        "_id" : "735126"
      },
      "rooli" : "osaamisala",
      "muodostumisSaanto" : {
        "laajuus" : {
          "minimi" : 135,
          "maksimi" : 135,
          "yksikko" : null
        },
        "koko" : null
      },
      "osaamisala" : {
        "nimi" : {
          "fi" : "Varaosamyynnin osaamisala",
          "sv" : "Kompetensområde för reservdelsförsäljning",
          "_id" : "185654"
        },
        "osaamisalakoodiArvo" : "1529",
        "osaamisalakoodiUri" : "osaamisala_1529"
      },
      "osat" : [ {
        "id" : 1273715,
        "kuvaus" : null,
        "vieras" : null,
        "nimi" : {
          "fi" : "Pakolliset tutkinnon osat",
          "sv" : "Obligatoriska examensdelar",
          "_id" : "941408"
        },
        "rooli" : "määritelty",
        "muodostumisSaanto" : {
          "laajuus" : {
            "minimi" : 90,
            "maksimi" : 90,
            "yksikko" : null
          },
          "koko" : null
        },
        "osaamisala" : null,
        "osat" : [ {
          "id" : 1273716,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : true,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7980"
        }, {
          "id" : 1273717,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : true,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "14635"
        }, {
          "id" : 1273718,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : true,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10050"
        } ],
        "versioId" : null
      }, {
        "id" : 1273719,
        "kuvaus" : null,
        "vieras" : null,
        "nimi" : {
          "fi" : "Valinnaiset tutkinnon osat",
          "sv" : "Valbara examensdelar",
          "_id" : "735127"
        },
        "rooli" : "määritelty",
        "muodostumisSaanto" : {
          "laajuus" : {
            "minimi" : 45,
            "maksimi" : 45,
            "yksikko" : null
          },
          "koko" : null
        },
        "osaamisala" : null,
        "osat" : [ {
          "id" : 1273720,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10052"
        }, {
          "id" : 1273721,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10053"
        }, {
          "id" : 1273722,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10056"
        }, {
          "id" : 1273723,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10059"
        }, {
          "id" : 1273724,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "11690"
        }, {
          "id" : 1273725,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "11691"
        }, {
          "id" : 1273726,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "11698"
        }, {
          "id" : 1273727,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "11699"
        }, {
          "id" : 1273728,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13290"
        }, {
          "id" : 1273729,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13295"
        }, {
          "id" : 1273730,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7441"
        }, {
          "id" : 1273731,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7442"
        }, {
          "id" : 1273732,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7443"
        }, {
          "id" : 1273733,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7444"
        }, {
          "id" : 1273734,
          "kuvaus" : null,
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammatillisesta perustutkinnosta",
            "sv" : "Examensdel från en yrkesinriktad grundexamen",
            "_id" : "735128"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 10,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273735,
          "kuvaus" : {
            "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp",
            "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
            "_id" : "735129"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammattitutkinnosta tai erikoisammattitutkinnosta",
            "sv" : "Examensdel från en yrkesexamen eller en specialyrkesexamen",
            "_id" : "735150"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : null,
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273736,
          "kuvaus" : {
            "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp.",
            "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
            "_id" : "735151"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammattikorkeakouluopinnoista",
            "sv" : "Examensdel från yrkeshögskolestudier",
            "_id" : "735152"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : null,
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273737,
          "kuvaus" : {
            "fi" : "Tämä voi sisältää työelämän alueellisten ja paikallisten ammattitaitovaatimusten ja opiskelijan ammattitaidon syventämistarpeiden mukaisia tutkinnon osia, joiden tulee vastata laajemmin paikallisiin ammattitaitovaatimuksiin kuin pelkästään yhden yrityksen tarpeisiin. Nämä tutkinnon osat nimetään työelämän toimintakokonaisuuden pohjalta ja niille määritellään laajuus osaamispisteinä (5-15 osp). Lisäksi määritellään tutkinnon osien ammattitaitovaatimukset, osaamisen arviointi ja ammattitaidon osoittamistavat Opetushallituksen määräyksen (Osaamisen tunnistamisen ja tunnustamisen mitoituksen periaatteet ja arvosanojen muuntaminen ammatillisessa peruskoulutuksessa 93/011/2014) liitteessä olevan arviointitaulukon mukaisesti.",
            "sv" : "Detta kan vara examensdelar som tillgodoser de regionala och lokala kraven på yrkesskicklighet och studerandes behov av att fördjupa sin yrkesskicklighet. Examensdelarna bör svara mot mer omfattande lokala krav på yrkesskicklighet än endast mot behoven hos ett enskilt företag. Dessa examensdelar nämns utifrån en verksamhetshelhet inom arbetslivet och omfattningen på dem bestäms utifrån kompetenspoäng (5-15 kp). Dessutom bestäms för examensdelarna kraven på yrkesskicklighet, bedömning av kunnandet och sätten att påvisa yrkesskickligheten enligt bedömningstabellen i bilagan i Utbildningsstyrelsens föreskrift. (Principer för dimensioneringen av identifiering och erkännande av kunnande samt omvandling av vitsord i den grundläggande yrkesutbildningen 93/011/2014).",
            "_id" : "735153"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Paikallisiin ammattitaitovaatimuksiin perustuvia tutkinnon osia",
            "sv" : "Examensdelar som grundar sig på lokala krav på yrkesskicklighet",
            "_id" : "735154"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 5,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273738,
          "kuvaus" : null,
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa vapaasti valittavista tutkinnon osista",
            "sv" : "Examensdel från de fritt valbara examensdelarna",
            "_id" : "735155"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 5,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        } ],
        "versioId" : null
      } ],
      "versioId" : null
    }, {
      "id" : 1273739,
      "kuvaus" : null,
      "vieras" : null,
      "nimi" : {
        "fi" : "Moottorikäyttöisten pienkoneiden korjauksen osaamisala",
        "sv" : "Kompetensområde för reparation av motordrivna småmaskiner",
        "_id" : "185657"
      },
      "rooli" : "osaamisala",
      "muodostumisSaanto" : {
        "laajuus" : {
          "minimi" : 135,
          "maksimi" : 135,
          "yksikko" : null
        },
        "koko" : null
      },
      "osaamisala" : {
        "nimi" : {
          "fi" : "Moottorikäyttöisten pienkoneiden korjauksen osaamisala",
          "sv" : "Kompetensområde för reparation av motordrivna småmaskiner",
          "_id" : "185655"
        },
        "osaamisalakoodiArvo" : "1622",
        "osaamisalakoodiUri" : "osaamisala_1622"
      },
      "osat" : [ {
        "id" : 1273740,
        "kuvaus" : null,
        "vieras" : null,
        "nimi" : {
          "fi" : "Pakolliset tutkinnon osat",
          "sv" : "Obligatoriska examensdelar",
          "_id" : "941409"
        },
        "rooli" : "määritelty",
        "muodostumisSaanto" : {
          "laajuus" : {
            "minimi" : 90,
            "maksimi" : 90,
            "yksikko" : null
          },
          "koko" : null
        },
        "osaamisala" : null,
        "osat" : [ {
          "id" : 1273741,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : true,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7449"
        }, {
          "id" : 1273742,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : true,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "10051"
        } ],
        "versioId" : null
      }, {
        "id" : 1273743,
        "kuvaus" : null,
        "vieras" : null,
        "nimi" : {
          "fi" : "Valinnaiset tutkinnon osat",
          "sv" : "Valbara examensdelar",
          "_id" : "735156"
        },
        "rooli" : "määritelty",
        "muodostumisSaanto" : {
          "laajuus" : {
            "minimi" : 45,
            "maksimi" : 45,
            "yksikko" : null
          },
          "koko" : null
        },
        "osaamisala" : null,
        "osat" : [ {
          "id" : 1273744,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "11698"
        }, {
          "id" : 1273745,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13291"
        }, {
          "id" : 1273746,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13292"
        }, {
          "id" : 1273747,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13293"
        }, {
          "id" : 1273748,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13299"
        }, {
          "id" : 1273749,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13294"
        }, {
          "id" : 1273750,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "13295"
        }, {
          "id" : 1273751,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7441"
        }, {
          "id" : 1273752,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7442"
        }, {
          "id" : 1273753,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7443"
        }, {
          "id" : 1273754,
          "kuvaus" : null,
          "vieras" : null,
          "pakollinen" : false,
          "erikoisuus" : null,
          "_tutkinnonOsaViite" : "7444"
        }, {
          "id" : 1273755,
          "kuvaus" : null,
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammatillisesta perustutkinnosta",
            "sv" : "Examensdel från en yrkesinriktad grundexamen",
            "_id" : "735157"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 10,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273756,
          "kuvaus" : {
            "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp",
            "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
            "_id" : "735158"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammattitutkinnosta tai erikoisammattitutkinnosta",
            "sv" : "Examensdel från en yrkesexamen eller en specialyrkesexamen",
            "_id" : "735159"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : null,
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273757,
          "kuvaus" : {
            "fi" : "Tämän valinnaisen tutkinnon osan laajuudeksi lasketaan 15 osp.",
            "sv" : "Denna valbara examensdel är till sin omfattning 15 kp.",
            "_id" : "735180"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa ammattikorkeakouluopinnoista",
            "sv" : "Examensdel från yrkeshögskolestudier",
            "_id" : "735181"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : null,
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273758,
          "kuvaus" : {
            "fi" : "Tämä voi sisältää työelämän alueellisten ja paikallisten ammattitaitovaatimusten ja opiskelijan ammattitaidon syventämistarpeiden mukaisia tutkinnon osia, joiden tulee vastata laajemmin paikallisiin ammattitaitovaatimuksiin kuin pelkästään yhden yrityksen tarpeisiin. Nämä tutkinnon osat nimetään työelämän toimintakokonaisuuden pohjalta ja niille määritellään laajuus osaamispisteinä (5-15 osp). Lisäksi määritellään tutkinnon osien ammattitaitovaatimukset, osaamisen arviointi ja ammattitaidon osoittamistavat Opetushallituksen määräyksen (Osaamisen tunnistamisen ja tunnustamisen mitoituksen periaatteet ja arvosanojen muuntaminen ammatillisessa peruskoulutuksessa 93/011/2014) liitteessä olevan arviointitaulukon mukaisesti.",
            "sv" : "Detta kan vara examensdelar som tillgodoser de regionala och lokala kraven på yrkesskicklighet och studerandes behov av att fördjupa sin yrkesskicklighet. Examensdelarna bör svara mot mer omfattande lokala krav på yrkesskicklighet än endast mot behoven hos ett enskilt företag. Dessa examensdelar nämns utifrån en verksamhetshelhet inom arbetslivet och omfattningen på dem bestäms utifrån kompetenspoäng (5-15 kp). Dessutom bestäms för examensdelarna kraven på yrkesskicklighet, bedömning av kunnandet och sätten att påvisa yrkesskickligheten enligt bedömningstabellen i bilagan i Utbildningsstyrelsens föreskrift. (Principer för dimensioneringen av identifiering och erkännande av kunnande samt omvandling av vitsord i den grundläggande yrkesutbildningen 93/011/2014).",
            "_id" : "735182"
          },
          "vieras" : null,
          "nimi" : {
            "fi" : "Paikallisiin ammattitaitovaatimuksiin perustuvia tutkinnon osia",
            "sv" : "Examensdelar som grundar sig på lokala krav på yrkesskicklighet",
            "_id" : "735183"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 5,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        }, {
          "id" : 1273759,
          "kuvaus" : null,
          "vieras" : null,
          "nimi" : {
            "fi" : "Tutkinnon osa vapaasti valittavista tutkinnon osista",
            "sv" : "Examensdel från de fritt valbara examensdelarna",
            "_id" : "735184"
          },
          "rooli" : "määrittelemätön",
          "muodostumisSaanto" : {
            "laajuus" : {
              "minimi" : 5,
              "maksimi" : 15,
              "yksikko" : null
            },
            "koko" : null
          },
          "osaamisala" : null,
          "osat" : [ ],
          "versioId" : null
        } ],
        "versioId" : null
      } ],
      "versioId" : null
    } ],
    "versioId" : null
                    }, {
                      "id" : 1273760,
                      "kuvaus" : {
                        "fi" : "Tutkinnon perusteissa määrättyjen osa-alueiden valinnaisten osaamistavoitteiden lisäksi tai niiden sijaan koulutuksen järjestäjä voi päättää erilaajuisia muita valinnaisia osaamistavoitteita. Koulutuksen järjestäjän itse päättämille valinnaisille osaamistavoitteille määritellään osaamisen arviointi ja laajuus osaamispisteinä. Tutkinnon osien valinnaiset osaamistavoitteet voivat olla myös opiskelijan aikaisemmin hankkimaa osaamista, joka tukee kyseisen tutkinnon osan ja sen osa-alueiden osaamistavoitteita.",
                        "sv" : "Förutom eller istället för de mål för kunnandet gällande de valbara delarna enligt det som föreskrivs i examensgrunderna, kan utbildningsanordnaren besluta om övriga valbara mål av olika omfattningar för kunnandet. För de valbara mål för kunnandet som utbildningsanordnaren själv beslutit om, bestäms bedömning av kunnandet och omfattning i kompetenspoäng. De valbara målen för kunnandet gällande delområdena kan även vara studerandes tidigare inhämtade kunnande som stöder målen för kunnandet i ifrågavarande examensdel eller dess delområden.",
                        "_id" : "735185"
                      },
                      "vieras" : null,
                      "nimi" : {
                        "fi" : "Yhteiset tutkinnon osat",
                        "sv" : "Gemensamma examensdelar",
                        "_id" : "735186"
                      },
                      "rooli" : "määritelty",
                      "muodostumisSaanto" : {
                        "laajuus" : {
                          "minimi" : 35,
                          "maksimi" : 35,
                          "yksikko" : null
                        },
                        "koko" : null
                      },
                      "osaamisala" : null,
                      "osat" : [ {
                        "id" : 1273761,
                        "kuvaus" : null,
                        "vieras" : null,
                        "pakollinen" : false,
                        "erikoisuus" : null,
                        "_tutkinnonOsaViite" : "7445"
                      }, {
                        "id" : 1273762,
                        "kuvaus" : null,
                        "vieras" : null,
                        "pakollinen" : false,
                        "erikoisuus" : null,
                        "_tutkinnonOsaViite" : "7446"
                      }, {
                        "id" : 1273763,
                        "kuvaus" : null,
                        "vieras" : null,
                        "pakollinen" : false,
                        "erikoisuus" : null,
                        "_tutkinnonOsaViite" : "7447"
                      }, {
                        "id" : 1273764,
                        "kuvaus" : {
                          "fi" : "Sosiaalinen ja kulttuurinen osaaminen -tutkinnon osaan voidaan sisällyttää muiden yhteisten tutkinnon osien osa-alueiden valinnaisia osaamistavoitteita",
                          "sv" : "Examensdelen Socialt och kulturellt kunnande kan innehålla valbara mål för kunnandet gällande delområden ur de övriga gemensamma examensdelarna.",
                          "_id" : "941604"
                        },
                        "vieras" : null,
                        "pakollinen" : false,
                        "erikoisuus" : null,
                        "_tutkinnonOsaViite" : "7448"
                      } ],
                      "versioId" : null
                    }, {
                      "id" : 1273765,
                      "kuvaus" : {
                        "fi" : "Vapaasti valittavat tutkinnon osat (10 osp) tukevat tutkinnon ammattitaitovaatimuksia ja osaamistavoitteita ja ne voivat koostua yhdestä tai useammasta tutkinnon osasta. Tutkinnon osat voivat olla seuraavia:",
                        "sv" : "De fritt valbara examensdelarna (10 kp) stöder kraven på yrkesskicklighet och mål för kunnandet i examen och de kan bestå av en eller flera examensdelar. Examensdelarna kan vara följande:",
                        "_id" : "735187"
                      },
                      "vieras" : null,
                      "nimi" : {
                        "fi" : "Vapaasti valittavat tutkinnon osat",
                        "sv" : "Fritt valbara examensdelar",
                        "_id" : "735188"
                      },
                      "rooli" : "määritelty",
                      "muodostumisSaanto" : {
                        "laajuus" : {
                          "minimi" : 10,
                          "maksimi" : 10,
                          "yksikko" : null
                        },
                        "koko" : null
                      },
                      "osaamisala" : null,
                      "osat" : [ {
                        "id" : 1273766,
                        "kuvaus" : {
                          "fi" : "Tämä voi sisältää ammatillisia tutkinnon osia oman tai jonkin muun koulu-tusalan ammatillisista perustutkinnoista, ammattitutkinnoista tai erikoisammattitutkinnoista.",
                          "sv" : "Dessa kan vara yrkesinriktade examensdelar från det egna utbildningsområdet eller från yrkesinriktade grundexamina, yrkesexamina eller specialyrkesexamina från något annat utbildningsområde.",
                          "_id" : "735189"
                        },
                        "vieras" : null,
                        "nimi" : {
                          "fi" : "Ammatillisia tutkinnon osia",
                          "sv" : "Yrkesinriktade examensdelar",
                          "_id" : "735200"
                        },
                        "rooli" : "määrittelemätön",
                        "muodostumisSaanto" : {
                          "laajuus" : {
                            "minimi" : null,
                            "maksimi" : 10,
                            "yksikko" : null
                          },
                          "koko" : null
                        },
                        "osaamisala" : null,
                        "osat" : [ ],
                        "versioId" : null
                      }, {
                        "id" : 1273767,
                        "kuvaus" : {
                          "fi" : "Tämä voi sisältää työelämän alueellisten ja paikallisten ammattitaitovaatimusten ja opiskelijan ammattitaidon syventämistarpeiden mukaisia tutkinnon osia, joiden tulee vastata laajemmin paikallisiin ammattitaitovaatimuksiin kuin pelkästään yhden yrityksen tarpeisiin. Nämä tutkinnon osat nimetään työelämän toimintakokonaisuuden pohjalta ja niille määritellään laajuus osaamispisteinä. Lisäksi määritellään tutkinnon osien ammattitaitovaatimukset, osaamisen arviointi ja ammattitaidon osoittamistavat Opetushallituksen määräyksen (Osaamisen tunnistamisen ja tunnustamisen mitoituksen periaatteet ja arvosanojen muuntaminen ammatillisessa peruskoulutuksessa 93/011/2014) liitteessä olevan arviointitaulukon mukaisesti.",
                          "sv" : "Denna kan innefatta examensdelar som tillgodoser de regionala och lokala kraven på yrkesskicklighet. Examensdelarna bör svara mot mer omfattande lokala krav på yrkesskicklighet än endast mot behoven hos ett enskilt företag. Dessa examensdelar nämns utifrån en verksamhetshelhet inom arbetslivet och omfattningen på dem bestäms utifrån kompetenspoäng. Dessutom bestäms för examensdelarna kraven på yrkesskicklighet, bedömningen av kunnandet och sätten att påvisa yrkesskickligheten enligt bedömningstabellen i bilagan i Utbildningsstyrelsens föreskrift (Principerna för dimensioneringen av identifiering och erkännande av kunnande och omvandling av vitsord i grundläggande yrkesutbildning 93/011/2014).",
                          "_id" : "735201"
                        },
                        "vieras" : null,
                        "nimi" : {
                          "fi" : "Paikallisiin ammattitaitovaatimuksiin perustuvia tutkinnon osia",
                          "sv" : "Examensdelar som grundar sig på lokala krav på yrkesskicklighet",
                          "_id" : "735202"
                        },
                        "rooli" : "määrittelemätön",
                        "muodostumisSaanto" : {
                          "laajuus" : {
                            "minimi" : null,
                            "maksimi" : 10,
                            "yksikko" : null
                          },
                          "koko" : null
                        },
                        "osaamisala" : null,
                        "osat" : [ ],
                        "versioId" : null
                      }, {
                        "id" : 1273768,
                        "kuvaus" : {
                          "fi" : "Tämä voi sisältää ammatillisen perustutkinnon yhteisiä tutkinnon osia tai niiden osa-alueita tai lukio-opintoja, jotka toteutetaan lukion opetussuunnitelman perusteiden mukaisesti.",
                          "sv" : "Dessa kan vara gemensamma examensdelar i en yrkesinriktad grundexamen, delområden av dessa examensdelar eller gymnasiestudier som genomförs i enlighet med grunderna för gymnasiets läroplan.",
                          "_id" : "1027095"
                        },
                        "vieras" : null,
                        "nimi" : {
                          "fi" : "Yhteisten tutkinnon osien osa-alueita tai lukio-opintoja",
                          "sv" : "Delområden av gemensamma examensdelar eller gymnasiestudier",
                          "_id" : "1273474"
                        },
                        "rooli" : "määrittelemätön",
                        "muodostumisSaanto" : {
                          "laajuus" : {
                            "minimi" : null,
                            "maksimi" : 10,
                            "yksikko" : null
                          },
                          "koko" : null
                        },
                        "osaamisala" : null,
                        "osat" : [ ],
                        "versioId" : null
                      }, {
                        "id" : 1273769,
                        "kuvaus" : {
                          "fi" : "Tämä voi sisältää jatko-opintovalmiuksia tai ammatillista kehittymistä tukevia tutkinnon osia. Nämä tutkinnon osat nimetään ja niille määritellään laajuus osaamispisteinä. Lisäksi voidaan määritellä tutkinnon osien ammattitaitovaatimukset tai osaamistavoitteet, osaamisen arviointi ja ammattitaidon osoittamistavat. Osaaminen voidaan koulutuksen järjestäjän päätöksellä arvioida asteikolla hyväksytty/hylätty. Mikäli käytetään arviointiasteikkoa T1-K3, määrittelyssä voidaan hyödyntää Opetushallituksen määräyksen (Osaamisen tunnistamisen ja tunnustamisen mitoituksen periaatteet ja arvosanojen muuntaminen ammatillisessa peruskoulutuksessa 93/011/2014) liitteessä olevaa arviointitaulukkoa.",
                          "sv" : "Dessa kan vara examensdelar som ger förutsättningar för fortsatta studier eller examensdelar som stöder den yrkesmässiga utvecklingen. Dessa examensdelar namnges och omfattningen bestäms som kompetens-poäng. För varje examensdel kan dessutom bestämmas kraven på yrkesskicklighet eller målen för kunnandet, bedömningen av kunnandet och sätten att påvisa yrkesskickligheten. Kunnandet kan enligt utbildningsanordnarens beslut bedömas enligt skalan godkänd/underkänd. Om bedömningsskalan N1–B3 tillämpas, kan bedöm-ningstabellen i bilagan till Utbildningsstyrelsens föreskrift (Principer för dimensioneringen av identifiering och erkännande av kunnande samt omvandling av vitsord \ti den grundläggande yrkesutbildningen 93/011/2014) användas.",
                          "_id" : "1197048"
                        },
                        "vieras" : null,
                        "nimi" : {
                          "fi" : "Jatko-opintovalmiuksia tai ammatillista kehittymistä tukevia opintoja",
                          "sv" : "Studier som stöder förutsättningarna för fortsatta studier eller yrkesmässig utveckling",
                          "_id" : "735206"
                        },
                        "rooli" : "määrittelemätön",
                        "muodostumisSaanto" : {
                          "laajuus" : {
                            "minimi" : null,
                            "maksimi" : 10,
                            "yksikko" : null
                          },
                          "koko" : null
                        },
                        "osaamisala" : null,
                        "osat" : [ ],
                        "versioId" : null
                      }, {
                        "id" : 1273770,
                        "kuvaus" : {
                          "fi" : "Työkokemuksen kautta hankittuun osaamiseen perustuvat yksilölliset tutkinnon osat voivat olla oman tai muiden alojen työssä, itsenäisessä ammatin harjoittamisessa tai yrittämisessä hankittua osaamista. Nämä tutkinnon osat nimetään ja niille määritellään laajuus osaamispisteinä. Lisäksi voidaan määritellä tutkinnon osien ammattitaitovaatimukset, osaamisen arviointi ja ammattitaidon osoittamistavat. Osaaminen voidaan koulutuksen järjestäjän päätöksellä arvioida asteikolla hyväksytty/hylätty. Mikäli käytetään arviointiasteikkoa T1-K3, määrittelyssä voidaan hyödyntää Opetushallituksen määräyksen (Osaamisen tunnistamisen ja tunnustamisen mitoituksen periaatteet ja arvosanojen muuntaminen ammatillisessa peruskoulutuksessa 93/011/2014) liitteessä olevaa arviointitaulukkoa.",
                          "sv" : "Individuella examensdelar som baserar sig på kunnande som förvärvats via arbetserfarenhet kan bestå av kunnande som förvärvats i arbete inom den egna branschen eller andra branscher, i arbete som självständig yrkesutövare eller vid drivande av ett företag. Dessa examensdelar namnges och omfattningen bestäms som kompetenspoäng. För varje examensdel kan dessutom bestämmas kraven på yrkesskicklighet eller målen för kunnandet, bedömningen av kunnandet och sätten att påvisa yrkesskickligheten. Kunnandet kan enligt utbildningsanordnarens beslut bedömas enligt skalan godkänd/underkänd. Om bedömningsskalan N1–B3 tillämpas, kan bedömningstabellen i bilagan till Utbildningsstyrelsens föreskrift (Principer för dimens-ioneringen av identifiering och erkännande avkunnande samt omvandling av vitsord i den grundläggande yrkesutbildningen 93/011/2014) användas.",
                          "_id" : "1197049"
                        },
                        "vieras" : null,
                        "nimi" : {
                          "fi" : "Työkokemuksen kautta hankittuun osaamiseen perustuvia yksilöllisiä tutkinnon osia",
                          "sv" : "Individuella examensdelar som baserar sig på kunnande som förvärvats via arbetserfarenhet",
                          "_id" : "735208"
                        },
                        "rooli" : "määrittelemätön",
                        "muodostumisSaanto" : {
                          "laajuus" : {
                            "minimi" : null,
                            "maksimi" : 10,
                            "yksikko" : null
                          },
                          "koko" : null
                        },
                        "osaamisala" : null,
                        "osat" : [ ],
                        "versioId" : null
                      } ],
                      "versioId" : null
                    }, {
                      "id" : 1273771,
                      "kuvaus" : {
                        "fi" : "Ammatillisessa peruskoulutuksessa opiskelija voi yksilöllisesti sisällyttää perustutkintoonsa enemmän tutkinnon osia, jos se on tarpeellista työelämän alakohtaisten tai paikallisten ammattitaitovaatimusten tai opiskelijan ammattitaidon syventämisen kannalta. Ne voivat olla ammatillisia tutkinnon osia tai paikallisiin ammattitaitovaatimuksiin perustuvia tutkinnon osia.",
                        "sv" : "Den studerande kan inom grundläggande yrkesutbildning individuellt ta in fler examensdelar om det är nödvändigt med tanke på arbetslivets branschvisa eller lokala krav på yrkesskicklighet eller en fördjupning av studerandes yrkeskompetens. De kan vara examensdelar ur grunder för yrkesinriktade examina eller examensdelar som baserar sig på lokala krav på yrkesskicklighet.",
                        "_id" : "735209"
                      },
                      "vieras" : null,
                      "nimi" : {
                        "fi" : "Tutkintoa yksilöllisesti laajentavat tutkinnon osat",
                        "sv" : "Examensdelar som individuellt utvidgar examen",
                        "_id" : "735220"
                      },
                      "rooli" : "määrittelemätön",
                      "muodostumisSaanto" : null,
                      "osaamisala" : null,
                      "osat" : [ ],
                      "versioId" : null
                    } ],
                    "versioId" : 127378
                }
            }
        },
        suorituspolku: {
            controller: ($scope, osa) => {}
        },
        kommentointi: {
            controller: ($scope, kommentit, osa, Varmistusdialogi, orgoikeudet, kayttaja, $stateParams) => {
                $scope.$$kommenttiMaxLength = {
                    maara: 1024
                };

                const rikastaKommentti = (kommentti) => {
                    // Sisällön kloonaus peruutusta varten
                    kommentti.$$sisaltoKlooni = kommentti.sisalto;
                    kommentti.$$vastaus = {
                        sisalto: "",
                        $$sisaltoKlooni: ""
                    };

                    // Oikeudet
                    kommentti.$$muokkausSallittu = kommentti.luoja === kayttaja.oidHenkilo;
                    kommentti.$$poistaSallittu = Oikeudet.onVahintaan("hallinta",
                            Oikeudet.opsOikeus($stateParams.opsId)) || kommentti.luoja === kayttaja.oidHenkilo;
                };

                $scope.kommentit = _(kommentit)
                    .forEach(k => rikastaKommentti(k))
                    .filter(kommentti => kommentti.parentId === 0)
                    .map((kommentti: any) => {
                        // Aseta lapset
                        kommentti.$$lapset = _.filter(kommentit, (lapsi: any) => {
                            if (lapsi.parentId === kommentti.id) {
                                return lapsi;
                            }
                        });

                        return kommentti;
                    })
                    .value();

                $scope.avaaTekstikentta = (kommentti) => {
                    kommentti.$$sisaltoKlooni = kommentti.sisalto;
                    kommentti.$$isMuokkaus = true;
                };

                $scope.vastaaKommentti = (kommentti, parentId) => {
                    kommentti.parentId = parentId;
                    kommentti.sisalto = kommentti.$$sisaltoKlooni;
                    osa.all("kommentit").post(kommentti)
                        .then((uusi) => {
                            rikastaKommentti(uusi);
                            uusi.$$lapset = [];
                            if (parentId === 0) {
                                $scope.kommentit.unshift(uusi);
                                kommentti.$$isVastaus = false;
                            } else {
                                let parentKommentti: any = _($scope.kommentit).find({ id: uusi.parentId });
                                parentKommentti.$$lapset.unshift(uusi);
                                parentKommentti.$$isVastaus = false;
                            }
                        });
                };

                $scope.lisaaKommentti = (kommentti) => {
                    $scope.vastaaKommentti(kommentti, 0);
                };

                $scope.muokkaaKommentti = (kommentti) => {
                    kommentti.sisalto = kommentti.$$sisaltoKlooni;
                    kommentti.save()
                        .then((uusi) => _.merge(kommentti, uusi.plain()));
                    kommentti.$$isMuokkaus = false;
                };

                $scope.poistaKommentti = (kommentti) => {
                    Varmistusdialogi.dialogi({
                        otsikko: 'vahvista-poisto',
                        teksti: 'poistetaanko-kommentti',
                        primaryBtn: 'poista',
                        successCb: () => {
                            kommentti.remove()
                                .then(() => {
                                    kommentti.poistettu = true;
                                    kommentti.sisalto = "";
                                });
                        }
                    })();
                };
            }
        }
    }
}));
