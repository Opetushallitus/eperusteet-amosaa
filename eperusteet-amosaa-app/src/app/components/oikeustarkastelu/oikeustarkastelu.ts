module OikeustarkasteluImpl {
    export const controller = ($rootScope, $scope) => {
        $scope.oikeudet = $rootScope.oikeudet;
    };

    export const directive = () => {
        return {
            restrict: "A",
            controller: controller,
            link: (scope, element, attrs) => {
                if (!_.some(attrs.oikeustarkastelu.split("|"), (oikeus) => oikeus === scope.oikeudet)) {
                    element.hide();
                }
            }
        }
    };
}

angular.module("app")
    .directive("oikeustarkastelu", OikeustarkasteluImpl.directive);
