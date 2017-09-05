angular.module("app").config($stateProvider =>
    $stateProvider.state("root.koulutustoimija.useinkysyttya", {
        url: "/faq",
        resolve: {
            faq: Api => Api.all("ohjeet").getList()
        },
        views: {
            "": {
                controller: ($rootScope, $scope, $location, $timeout, $stateParams, koulutustoimija, faq) => {
                    const contains = (target, search) =>
                        _.isString(target) &&
                        _.isString(search) &&
                        target.toLowerCase().indexOf(search.toLowerCase()) !== -1;

                    $scope.koulutustoimija;
                    $scope.faq = faq.reverse();
                    $scope.canEdit = Koulutustoimijat.isOpetushallitus(koulutustoimija);
                    $scope.search = "";

                    $scope.suodata = search => {
                        _.each($scope.faq, item => {
                            item.$$filtered =
                                !search ||
                                _.isEmpty(search) ||
                                contains(item.kysymys, search) ||
                                contains(item.vastaus, search);
                        });
                    };

                    $scope.add = () => {
                        $scope.faq.unshift({
                            kysymys: "",
                            vastaus: "",
                            $$filtered: true
                        });
                        $scope.start($scope.faq[0]);
                    };

                    $scope.start = item => {
                        $scope.$$isEditing = true;
                        item.$$kysymys = item.kysymys;
                        item.$$vastaus = item.vastaus;
                        item.$$editing = true;
                    };

                    $scope.save = item => {
                        $rootScope.$broadcast("notifyCKEditor");
                        if (item.id) {
                            faq.customPUT(item, item.id);
                        } else {
                            faq.post(item).then(res => _.merge(item, res));
                        }
                        item.$$editing = false;
                        $scope.$$isEditing = false;
                    };

                    $scope.cancel = item => {
                        item.$$editing = false;
                        item.kysymys = item.$$kysymys;
                        item.vastaus = item.$$vastaus;
                        if (!item.id) {
                            _.remove($scope.faq, item);
                        }
                        $scope.$$isEditing = false;
                    };

                    $scope.remove = item => {
                        item.remove();
                        _.remove($scope.faq, item);
                        $scope.$$isEditing = false;
                    };

                    $scope.suodata();
                }
            }
        }
    })
);
