


class Mp3Ctrl
 
    constructor: (@$log, @$scope, @$rootScope, @Mp3Service, @$filter,@$window) ->
        @$log.debug "constructing Mp3Controller"
        @currentTrack = 0
        @currentIndex = 0
        @pageSize = 5
        @mp3s = []
        @mp3 = []
        @mp3Data = []
        @playlst = []
        @artists = []
        @artist = []
        @albums = []
        @album = []
        @genres = []
        @genre = []
        @title = []
        @orderByStr = 'index'
        @orderBy = $filter('orderBy');
        @getAllMp3s()
        @getAllArtists()
        @getAllAlbums()
        @getAllGenres()
        @isDebug = false
        @shuffle = false
        @init( this)
       
  		
   
 
    init: (us) ->
       
       @$scope.$on 'audio.next',() ->
         us.next() 
     
       @$scope.$on 'audio.prev',() ->
         us.prev()

       @$rootScope.$on 'audio.ended',() ->
         us.next() 
       
       @$scope.$watch   -> 
         console.log('Watcher')
       
 
    @gridOptions = {
      data:                'this.mp3s',
      multiSelect:         false,  
      enableCellSelection: false,
      enableRowSelection:  true,
      rowTemplate:         '<div ng-click="onClickOrderRow(row)" ng-style="{ \'cursor\': row.cursor }" ng-repeat="col in renderedColumns" ng-class="col.colIndex()" class="ngCell {{col.cellClass}}"><div class="ngVerticalBar" ng-style="{height: rowHeight}" ng-class="{ ngVerticalBarVisible: !$last }">&nbsp;</div><div ng-cell></div></div>',
      columnDefs: [
        { field: 'songTitle',    displayName: 'Song Title',           width: '50%' },
        { field: 'artist',       displayName: 'Artist',       width: '50%' }
      ],
    }
  
    onClickOrderRow:(row) ->
      console.log(row.Entity.toString)
      
    
                 
    next: () ->        
      #console.log('Next IDX ' + @currentIndex  + ' curr: ' + @playlst[@currentIndex] + ' next: ' +  @playlst[@currentIndex+1] + ' second:' +  @playlst[@currentIndex+2])
      #console.log('Next Song ' + @mp3s[@playlst[@currentIndex+1]].songTitle + ' id: ' +   @mp3s[@playlst[@currentIndex+1]].index )
      if(@shuffle== true)
         @currentIndex++
         if (@currentIndex <@mp3s.length)
           @currentTrack = @playlst[@currentIndex]
           @updateTrack();
         else
            @currentTrack= playlst[@mp3s.length-1];
      else      
        @currentTrack++;
        if (@currentTrack <@mp3s.length)
          @updateTrack();
        else
          @currentTrack= @mp3s.length-1;
     
                 
    prev: () -> 
      if(@shuffle== true)
        @currentIndex--
        if (@currentIndex >= 0)
          @currentTrack = @playlst[@currentIndex]
          @updateTrack();
        else
           @currentTrack= playlst[0];
      else 
        @currentTrack--;
        if (@currentTrack >= 0)
          @updateTrack();
        else
          @currentTrack = 0;
  
     
    updateTrack: () ->
      @$log.debug "updateTrack() #{@mp3s[@currentTrack].songTitle} ShuffleID: #{@mp3s[@currentTrack].shuffleId}  index: #{@mp3s[@currentTrack].index}   #{@playlst[@currentIndex]}" 
      @$rootScope.$broadcast('audio.set', 'mp3/'+ @mp3s[@currentTrack]._id.$oid,  @mp3s[@currentTrack], @currentTrack, @mp3s.length);
      @$rootScope.$broadcast('audio.play',this) 
      document.getElementById(@currentTrack).scrollIntoView()
  
       
           
     getAllMp3s: () ->
       @$log.debug "getAllMp3s()"
       @Mp3Service.listMp3s()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
                @$rootScope.$broadcast('audio.set', 'mp3/'+ @mp3s[@currentTrack]._id.$oid,  @mp3s[@currentTrack], @currentTrack, @mp3s.length);
                @currentIndex = 0
                #@randomize()
                index = 0
                ( mp.index=index;  index++;) for mp in @mp3s                
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )
        
            
     getMp3sByArtist: (artist) ->
       @$log.debug "getMp3sByArtist(#{artist})"
       @Mp3Service.listMp3sByArtist(artist)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
                @$rootScope.$broadcast('audio.set', 'mp3/'+ @mp3s[@currentTrack]._id.$oid,  @mp3s[@currentTrack], @currentTrack, @mp3s.length);
                @currentIndex = 0
                #@randomize()
                index = 0
                (mp.index=index++; mp.shuffleId = @playlst[index]) for mp in @mp3s                 
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )

     getMp3sByAlbum: (album) ->
       @$log.debug "getMp3sByAlbum(#{album})"
       @Mp3Service.listMp3sByAlbum(album)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
                @$rootScope.$broadcast('audio.set', 'mp3/'+ @mp3s[@currentTrack]._id.$oid,  @mp3s[@currentTrack], @currentTrack, @mp3s.length);
                @currentIndex = 0
                #@randomize()
                index = 0
                (mp.index=index++; mp.shuffleId = @playlst[index]) for mp in @mp3s               
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )     
            
     getMp3sByGenre: (genre) ->
       @$log.debug "getMp3sByGenre(#{genre})"
       @Mp3Service.listMp3sByGenre(genre)
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} Mp3s"
                @mp3s = angular.copy data
                @$rootScope.$broadcast('audio.set', 'mp3/'+ @mp3s[@currentTrack]._id.$oid,  @mp3s[@currentTrack], @currentTrack, @mp3s.length);
                @currentIndex = 0
                #@randomize()
                index = 0
                (mp.index=index++; mp.shuffleId = @playlst[index]) for mp in @mp3s               
            ,
            (error) =>
                @$log.error "Unable to get Mp3: #{error}"
            )            

     getAllArtists: () ->
       @$log.debug "getAllArtists()"
       @Mp3Service.listArtists()
        .then(
            (data) =>
                @$log.debug "f Promise returned #{data.length} artists"
                
                @artists = angular.copy data
                           
            ,
            (error) =>
                @$log.error "Unable to get artists: #{error}"
            )
            

     getAllAlbums: () ->
       @$log.debug "getAllAlbums()"
       @Mp3Service.listAlbums()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} albums"
                
                @albums = angular.copy data
                          
            ,
            (error) =>
                @$log.error "Unable to get albums: #{error}"
            )
             
     getAllGenres: () ->
       @$log.debug "getAllGenres()"
       @Mp3Service.listGenres()
        .then(
            (data) =>
                @$log.debug "Promise returned #{data.length} genres"
                
                @genres = angular.copy data
                          
            ,
            (error) =>
                @$log.error "Unable to get genres: #{error}"
            )   
                    
     playMp3:(mp3info)  ->
       @$log.debug "playMp3s()" + mp3info
       @title = mp3info.filePath
       @mp3 = mp3info
       @currentTrack= @mp3.index
       @updateTrack()
     
     #order:(predicate,reverse) ->
     #  @mp3s = @orderBy(@mp3s,predicate,reverse)
     
     doRandom:() ->
       if(@shuffle)
         this.randomize()
         @orderByStr = 'shuffleId'
       else
         @orderByStr = 'index'
       
         
     randomize:() ->
       count = @mp3s.length
       @$log.debug " randomize(#{count})" 
       @playlst = []
       for i in [0..count] by 1
         loop
           @tv = Math.floor((Math.random()*count))
           
           break if( @tv not in @playlst)
           break if (i == count)
         #@$log.debug " rnd# (#{@tv} #{@mp3s[@tv].songTitle})"   
         @mp3s[@tv].shuffleId = i  
         @playlst.push(@tv)
        
    
      
     controllersModule.controller('Mp3Ctrl', Mp3Ctrl)
     