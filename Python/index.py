from libs.EndpointConfiguration import EndpointConfiguration

# endpoint_configuration = EndpointConfiguration(file_name='test.txt')
endpoint_configuration = EndpointConfiguration(file_name='me_at_the_zoo.in')

print endpoint_configuration.videos
print endpoint_configuration.endpoints
print endpoint_configuration.caches
print endpoint_configuration.video_request

list1 = []
for endpoint_index in endpoint_configuration.endpoints:
    current_endpoint = endpoint_configuration.endpoints[endpoint_index]
    for video_id in current_endpoint['video_request']:
        video_request = current_endpoint['video_request'][video_id]

        list1.append(
            {
                'cache_id': -1,
                'video_id': video_id,
                'weight': video_request * current_endpoint['datacenter_latency']
            }
        )

        for cache_index in current_endpoint['caches']:
            list1.append(
                {
                    'cache_id': cache_index,
                    'video_id': video_id,
                    'weight': video_request * current_endpoint['caches'][cache_index]
                }
            )

            # for item in list1:
            #   print item

# cache_intermediate = {}
list2 = []

list3 = {}

for cache_index in endpoint_configuration.caches:
    list3[cache_index] = {}

    cache_item = endpoint_configuration.caches[cache_index]

    for endpoint_index in cache_item:
        current_endpoint = endpoint_configuration.endpoints[endpoint_index]
        for video_id in current_endpoint['video_request']:
            video_request = current_endpoint['video_request'][video_id]

            if video_id not in list3[cache_index] or cache_index not in list3:
                # add it
                if cache_index not in list3:
                    list3[cache_index] = {}

                list3[cache_index][video_id] = (current_endpoint['datacenter_latency'] - current_endpoint['caches'][
                    cache_index]) * video_request
            else:
                # update it
                list3[cache_index][video_id] = list3[cache_index][video_id] + (current_endpoint['datacenter_latency'] -
                                                                               current_endpoint['caches'][
                                                                                   cache_index]) * video_request

            list2.append(
                {
                    'cache_id': cache_index,
                    'endpoint_id': endpoint_index,
                    'video_id': video_id,
                    'weight': (current_endpoint['datacenter_latency'] - current_endpoint['caches'][
                        cache_index]) * video_request
                }
            )

print list3

for cache_index in list3:
    cache_item = list3[cache_index]

    available_cache_size = endpoint_configuration.cache_size
    for video_id in sorted(cache_item.iterkeys(), reverse=False):
        if (available_cache_size > 0):

            available_cache_size -= endpoint_configuration.videos[video_id]
