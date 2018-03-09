angular.module("app")
    .directive("smallSpinner", function() {
        return {
            restrict: "EA",
            link: function(scope, element) {
                element.prepend('<img class="small-spinner" src="images/spinner-small.gif" alt="">');
            }
        };
    });

