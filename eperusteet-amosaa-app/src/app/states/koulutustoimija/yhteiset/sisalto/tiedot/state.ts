angular.module("app")
.config($stateProvider => $stateProvider
    .state("root.koulutustoimija.yhteiset.sisalto.tiedot", {
        url: "/tiedot",
        resolve: {
            historia: (yhteiset) => yhteiset.all("versiot").getList()
        },
        controller: ($q, $scope, koulutustoimija, yhteiset, historia) => {
            $scope.koulutustoimija = koulutustoimija;
            [$scope.uusin, $scope.historia] = Revisions.parseAll(historia);

            $scope.edit = EditointikontrollitService.createRestangular($scope, "yhteiset", yhteiset, {
                done: () => historia.get("uusin").then(res => {
                    console.log(res);
                    $scope.uusin = Revisions.parseOne(res);
                    $scope.historia.push($scope.uusin);
                })
            });

            $scope.listRevisions = () => ModalRevisions.viewRevisions($scope.historia)
                .then(res => {
                    // TODO vaihda versio
                });
        }
    }));
