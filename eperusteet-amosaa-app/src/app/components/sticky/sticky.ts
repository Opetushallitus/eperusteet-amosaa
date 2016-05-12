namespace Sticky {
    export const directive = () => {
        return {
            restrict: "A",
            link: (scope, element, attrs) => {
                $(element).sticky({
                    topSpacing: attrs.topSpacing || 0,
                    className: attrs.classname
                });
            }
        }
    };
}

angular.module("app")
.directive("sticky", Sticky.directive);
