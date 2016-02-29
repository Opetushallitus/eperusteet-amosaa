namespace OikeudetService {
    let _$state;

    export const init = ($state) => {
        _$state = $state;
    };

}

module OikeustarkasteluImpl {
    export const directive = () => {
        return {
            restrict: "A",
            link: (scope, element, attrs) => {
                Kayttaja.oikeudet().then(res => {
                    console.log(res);
                });
            }
        }
    };
}

angular.module("app")
    .run(($injector) => $injector.invoke(OikeudetService.init))
    .directive("oikeustarkastelu", OikeustarkasteluImpl.directive);
