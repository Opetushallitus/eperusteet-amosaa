namespace Taulukot {
    export const bindSivutus = ($scope, jarjestys: string, poistetut: Array<any>) => {
        $scope.poistetut = poistetut;
        $scope.nimi = "";

        $scope.jarjestys = jarjestys;
        $scope.jarjestysKaanteinen = false;
        $scope.sivu = 1;
        $scope.alkioitaSivulla = 20;

        return (uusiJarjestys: string) => {
            if ($scope.jarjestys === uusiJarjestys) {
                $scope.jarjestysKaanteinen = $scope.jarjestysKaanteinen === false;
            }
            $scope.jarjestys = uusiJarjestys;
        };
    };
}
