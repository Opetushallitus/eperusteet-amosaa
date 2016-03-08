namespace ModalRevisions {
    let i;
    export const init = ($injector) => {
        i = inject($injector, ["$rootScope", "$uibModal", "$q"]);
    };

    export const viewRevisions = (revisions) => i.$uibModal.open({
        resolve: {
            revisions: () => revisions
        },
        templateUrl: "modals/revisions/revisions.jade",
        controller: ($uibModalInstance, $scope, $state, revisions) => {
            $scope.update = (search) => {
                $scope.frevisions = _.filter(revisions, (rev: any) => _.matchStrings($scope.search, rev.kommentti));
                $scope.pagination = Pagination.paginate($scope.frevisions, 8);
                $scope.selectPage();
            };

            $scope.selectPage = (page = 0) => {
                $scope.pagination.current = page;
                $scope.revisions = Pagination.selectItems($scope.frevisions, $scope.pagination);
            };

            $scope.toggle = (revision) => revision.$$show = !revision.$$show;
            $scope.ok = $uibModalInstance.close;

            $scope.update();
        }
    }).result;

    export const kasiteRevision = (kasite) => i.$uibModal.open({
        size: 'lg',
        templateUrl: "modals/revisions/kasiteRevisionModal.jade",
        controller: ($uibModalInstance, $rootScope, $scope, $state, $q) => {

           $scope.kasite = kasite;

            $scope.edit = EditointikontrollitService.createLocal({
                start: () => $q((resolve, reject) => $scope.kasite.get()
                    .then(res => {
                        _.merge(kasite, res);
                        $scope.kasite = kasite.clone();
                        resolve();
                    })
                    .catch(reject)),
                save: () => $q((resolve, reject) => {
                    $rootScope.$broadcast("notifyCKEditor");
                    return $scope.kasite.put()
                        .then((res) => {
                            $uibModalInstance.close;
                            NotifikaatioService.onnistui("tallennus-onnistui");
                            return resolve(res);
                        })
                        .catch(reject);
                }),
                cancel: (res) => $q((resolve, reject) => {
                    $scope.kasite = kasite.clone();
                    resolve();
                })
            });

            $scope.cancel = EditointikontrollitService.cancel;
            $scope.save = EditointikontrollitService.save;
        }
    }).result;

};

angular.module("app")
.run(ModalRevisions.init);
