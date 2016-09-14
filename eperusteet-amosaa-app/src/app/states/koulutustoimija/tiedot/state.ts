angular.module("app")
.config($stateProvider => $stateProvider
.state("root.koulutustoimija.tiedot", {
    url: "/tiedot",
    resolve: {
        ystavat: koulutustoimija => koulutustoimija.all("ystavat").getList(),
        pyynnot: koulutustoimija => koulutustoimija.all("ystavapyynnot").getList()
    },
    controller: ($scope, $q, $state, $stateParams, $timeout, koulutustoimija, ystavat, pyynnot) => {
        $scope.koulutustoimija = koulutustoimija;
        $scope.kaverit = Algoritmit.doSortByNimi(ystavat);
        $scope.pyynnot = _.map(Algoritmit.doSortByNimi(pyynnot), (pyynto) => _.merge(pyynto, { status: "odottaa" }));

        $scope.edit = EditointikontrollitService.createLocal({
            start: () => $q((resolve, reject) => {
                resolve();
            }),
            save: () => $scope.koulutustoimija.save(),
            cancel: () => $q((resolve, reject) => {
                resolve();
                $timeout(() => $state.reload($state.current, $stateParams, { reload: true }));
            }),
        });

        $scope.lopetaYhteistyo = (org) => {
            _.remove($scope.kaverit, org)
            _.remove($scope.koulutustoimija.ystavat, (ystava) => ystava == org.id)
        };

        $scope.hyvaksyYhteistyo = (org) => {
            _.remove($scope.pyynnot, org);
            org.status = "yhteistyo";
            lisaaKaveri(org);
        };

        const lisaaKaveri = (data) => {
            const alreadyHas = _.some($scope.kaverit, (kaveri: any) => kaveri.id === data.id);
            if (!alreadyHas && data.id !== koulutustoimija.id) {
                $scope.koulutustoimija.ystavat.push(data.id);
                $scope.koulutustoimija.ystavat = _.uniq($scope.koulutustoimija.ystavat);
                $scope.kaverit.push({
                    id: data.id,
                    nimi: data.nimi,
                    status: data.status || "odotetaan"
                });
            }
        };

        $scope.cancel = EditointikontrollitService.cancel;
        $scope.save = EditointikontrollitService.save;
        $scope.lisaa = () => {
            Yhteistyo.modal(koulutustoimija)
                .then(lisaaKaveri);
        };
    }
}));
