(function () {
  var JPGCtrl;
  this.__indexOf = [].indexOf || function (item) { for (var i = 0, l = this.length; i < l; i++) { if (i in this && this[i] === item) return i; } return -1; };

  JPGCtrl = (function () {

        function JPGCtrl($log, $scope, $rootScope, JPGService, $filter, $window) {
            this.$log = $log;
            this.$scope = $scope;
            this.$rootScope = $rootScope;
            this.JPGService = JPGService;
            this.$filter = $filter;
            this.$window = $window;
            this.$log.debug("constructing JPGController");
            this.currentImg = 0;
            this.image = "";
            this.currentIndex = 0;
            this.pageSize = 5;
            this.jpgs = [];
            this.jpg = [];
            this.orderByStr = 'index';
            this.orderBy = $filter('orderBy');
            this.getAllJPGs();
            this.isDebug = false;
            this.shuffle = false;
            this.init(this);
        }

        JPGCtrl.prototype.init = function () {
            
            return this.$scope.$watch(function () {
                return console.log('Watcher');
            });
        };

        JPGCtrl.gridOptions = {
            data: 'this.jpgs',
            multiSelect: false,
            enableCellSelection: false,
            enableRowSelection: true,
            rowTemplate: '<div ng-click="onClickOrderRow(row)" ng-style="{ \'cursor\': row.cursor }" ng-repeat="col in renderedColumns" ng-class="col.colIndex()" class="ngCell {{col.cellClass}}"><div class="ngVerticalBar" ng-style="{height: rowHeight}" ng-class="{ ngVerticalBarVisible: !$last }">&nbsp;</div><div ng-cell></div></div>',
            columnDefs: [
              {
                  field: 'filePath',
                  displayName: 'Image file',
                  width: '50%'
              }, {
                  field: 'date',
                  displayName: 'date',
                  width: '50%'
              }
            ]
        };

        JPGCtrl.prototype.onClickOrderRow = function (row) {
            return console.log(row.Entity.toString);
        };

        JPGCtrl.prototype.getAllJPGs = function () {
            var _this = this;
            this.$log.debug("getAllJPGs()");
            return this.JPGService.listJPGs().then(function (data) {
                var index, mp, _i, _len, _ref, _results;
                _this.$log.debug("Promise returned " + data.length + " JPGs");
                _this.jpgs = angular.copy(data);
              
                _this.currentIndex = 0;
                index = 0;
                _ref = _this.jpgs;
                _results = [];
                for (_i = 0, _len = _ref.length; _i < _len; _i++) {
                    mp = _ref[_i];
                    _results.push((mp.index = index, index++));
                }
                return _results;
            }, function (error) {
                return _this.$log.error("Unable to get JPG: " + error);
            });
        };

      
        JPGCtrl.prototype.showJPG = function (jpginfo) {
            this.$log.debug("G1-show jpg()" + jpginfo);
            //var foo = this.ObjtoString(jpginfo._id);
            var foo = jpginfo._id.$oid;
            this.image =  "/JPG/"+foo;  //   this.JPGService.GetJPG( foo);
            
        };

     

        JPGCtrl.prototype.ObjtoString = function (ObjId) {
            var Timestamp = ObjId.Timestamp.toString(16);
            var Machine = ObjId.Machine.toString(16);
            var Pid = ObjId.Pid.toString(16);
            var Increment = ObjId.Increment.toString(16);
            return '00000000'.substr(0, 6 - Timestamp.length) + Timestamp +
                '000000'.substr(0, 6 - Machine.length) + Machine +
                '0000'.substr(0, 4 - Pid.length) + Pid +
                '000000'.substr(0, 6 - Increment.length) + Increment;
        };
        controllersModule.controller('JPGCtrl', JPGCtrl);


        return JPGCtrl;

    })();

}).call(this);
