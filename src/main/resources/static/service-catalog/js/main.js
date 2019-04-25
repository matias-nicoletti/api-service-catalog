function contains(string, key) {
	if (!key) {
		return true;
	}
	if (!string) {
		return false;
	}
	return string.toUpperCase().indexOf(key.toUpperCase()) !== -1;
}

var app = angular.module('app', []);
app.controller('ControllerApp', function($scope, $http, $timeout) {

	$scope.selectFirst = function() {
		$scope.currentService = null;
		for (i = 0; i < $scope.services.length; i++) {
			if ($scope.services[i].visible) {
				$scope.currentService = $scope.services[i];
				return;
			}
		}
	}

	$scope.filter = function() {
		for (i = 0; i < $scope.services.length; i++) {
			var fullServiceVisible = contains($scope.services[i].name, $scope.filterKey);
			$scope.services[i].visible = fullServiceVisible;
			var swaggerPaths = $scope.services[i].swagger.paths;

			for ( var k in swaggerPaths) {
				for ( var k2 in swaggerPaths[k]) {
					var endpointVisible = (
							fullServiceVisible ||
							contains(swaggerPaths[k][k2].summary,$scope.filterKey)||
							contains(k,$scope.filterKey) ||
							contains(k2,$scope.filterKey)
					);

					if (!endpointVisible && swaggerPaths[k][k2].tags!= null &&
							swaggerPaths[k][k2].tags.length>0 &&
							contains(swaggerPaths[k][k2].tags[0],$scope.filterKey)){
							endpointVisible = contains(swaggerPaths[k][k2].tags[0],$scope.filterKey);
							}

					swaggerPaths[k][k2].visible = endpointVisible;
					if (!fullServiceVisible && endpointVisible){
						$scope.services[i].visible = true;
					}
				}
			}
			$scope.services[i].swagger.paths = swaggerPaths;
		}
		$scope.selectFirst();

	}

	$scope.loadData = function() {
		$http.get(
				'service-catalog/services/endpoints?r='
						+ new Date().getTime()).success(function(data) {

			// Sets all visible by default
			for (i = 0; i < data.length; i++) {
				data[i].visible = true;
				for ( var k in data[i].swagger.paths) {
					for ( var k2 in data[i].swagger.paths[k]) {
						data[i].swagger.paths[k][k2].visible = true;
					}
				}
			}
			$scope.filterKey = "";
			$scope.services = data;
			$scope.selectFirst();
		});
	};

	$scope.showServiceDetail = function(service) {
		$scope.currentService = service;
	}

	$scope.showEndpointDetail = function(method, url, body) {
		$scope.currentEndpointDetail = {
			method : method,
			url : url,
			data : body
		};
	}

	$scope.runAsynchronousUpdate = function() {
		$http.put(
				'service-catalog/services/swaggers?r='
						+ new Date().getTime()).success(function(data) {
		});
	}

	$scope.loadData();

});