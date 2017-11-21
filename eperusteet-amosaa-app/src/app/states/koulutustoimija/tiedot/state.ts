angular.module("app")
    .directive("tietoOrganisaatio", () => ({
        restrict: "AE",

        scope: {
            organisaatio: "="
        },
        controller($scope) {
        }
    }))
    .config($stateProvider =>
        $stateProvider.state("root.koulutustoimija.tiedot", {
            url: "/tiedot",
            resolve: {
                ystavat: koulutustoimija => koulutustoimija.all("ystavat").getList(),
                pyynnot: koulutustoimija => koulutustoimija.all("ystavapyynnot").getList(),
                hierarkia: (koulutustoimija) => koulutustoimija.one("hierarkia").get(),
            },
            controller: ($scope, $q, $state, $stateParams, $timeout, koulutustoimija, ystavat, pyynnot, hierarkia) => {
                $scope.koulutustoimija = koulutustoimija;
                $scope.kaverit = Algoritmit.doSortByNimi(ystavat);
                $scope.pyynnot = _.map(Algoritmit.doSortByNimi(pyynnot), pyynto => _.merge(pyynto, { status: "odottaa" }));
                const kaveritMap = _($scope.kaverit).indexBy("organisaatio").value();
                $scope.hierarkia = _.flatten(updateHierarkia(hierarkia), true);
                $scope.hierarkiaMap = _($scope.hierarkia).indexBy("oid").value();

                function updateHierarkia(org, depth = 0) {
                    const lapsiorganisaatiot = _.map(org.children, (corg) => updateHierarkia(corg, depth + 1));
                    const result = [
                        {
                            ...org,
                            depth,
                            isOma: org.oid === koulutustoimija.organisaatio,
                            status: !!kaveritMap[org.oid] ? "yhteistyo" : ""
                        },
                        ...lapsiorganisaatiot
                    ];
                    return result;
                }

                $scope.edit = EditointikontrollitService.createLocal({
                    start: () =>
                        $q((resolve, reject) => {
                            resolve();
                        }),
                    save: () => $scope.koulutustoimija.save(),
                    cancel: () =>
                        $q((resolve, reject) => {
                            resolve();
                            $timeout(() => $state.reload($state.current, $stateParams, { reload: true }));
                        })
                });

                $scope.lopetaYhteistyo = org => {
                    _.remove($scope.kaverit, org);
                    _.remove($scope.koulutustoimija.ystavat, ystava => ystava == org.id);
                    _.remove($scope.kaveritMap, org.oid);
                };

                $scope.hyvaksyYhteistyo = org => {
                    _.remove($scope.pyynnot, org);
                    org.status = "yhteistyo";
                    lisaaKaveri(org);
                };

                const lisaaKaveri = data => {
                    const alreadyHas = _.some($scope.kaverit, (kaveri: any) => kaveri.id === data.id);
                    if (!alreadyHas && data.id !== koulutustoimija.id) {
                        $scope.koulutustoimija.ystavat.push(data.id);
                        $scope.koulutustoimija.ystavat = _.uniq($scope.koulutustoimija.ystavat);
                        const kaveri = {
                            id: data.id,
                            nimi: data.nimi,
                            status: data.status || "odotetaan"
                        };
                        $scope.kaverit.push(kaveri);
                        kaveritMap[data.oid] = kaveri;
                    }
                };

                $scope.cancel = EditointikontrollitService.cancel;
                $scope.save = EditointikontrollitService.save;
                $scope.lisaa = () => {
                    Yhteistyo.modal(koulutustoimija).then(lisaaKaveri);
                };
            }
        })
    );
